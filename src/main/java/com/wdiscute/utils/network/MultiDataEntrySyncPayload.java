package com.wdiscute.utils.network;

import com.wdiscute.utils.DataEntry;
import com.wdiscute.utils.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.nikdo53.neobackports.io.StreamCodec;
import net.nikdo53.neobackports.io.networking.CustomPacketPayload;
import net.nikdo53.neobackports.io.networking.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record MultiDataEntrySyncPayload(List<Map.Entry<DataEntry.MultiEntry<?>, List<?>>> entries) implements CustomPacketPayload
{

    public static final Type<MultiDataEntrySyncPayload> TYPE =
            new Type<>(Utils.rl("wdutils", "multi_entry_sync"), MultiDataEntrySyncPayload.class);

    public static final StreamCodec<MultiDataEntrySyncPayload> STREAM_CODEC =
            StreamCodec.of(
                    MultiDataEntrySyncPayload::encode,
                    MultiDataEntrySyncPayload::decode
            );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public void handle(IPayloadContext context)
    {
        context.enqueueWork(() ->
        {
            for (var entry : entries)
            {
                DataEntry.MultiEntry.MAP.put(entry.getKey(), entry.getValue());
            }
        });
    }

    private static void encode(FriendlyByteBuf buf, MultiDataEntrySyncPayload payload)
    {
        buf.writeVarInt(payload.entries.size());

        for (var entry : payload.entries)
        {
            DataEntry.MultiEntry<?> multiEntry = entry.getKey();

            buf.writeResourceLocation(multiEntry.path());

            writeList(buf, multiEntry, entry.getValue());
        }
    }

    private static MultiDataEntrySyncPayload decode(FriendlyByteBuf buf)
    {
        int size = buf.readVarInt();

        List<Map.Entry<DataEntry.MultiEntry<?>, List<?>>> entries = new ArrayList<>(size);

        for (int i = 0; i < size; i++)
        {
            ResourceLocation rl = buf.readResourceLocation();

            DataEntry.MultiEntry<?> multiEntry = DataEntry.MultiEntry.SYNC_ENTRIES_BY_ID.get(rl);

            if (multiEntry == null)
                throw new IllegalStateException("Unknown MultiEntry: " + rl);

            List<?> values = readList(buf, multiEntry);

            entries.add(Map.entry(multiEntry, values));
        }

        return new MultiDataEntrySyncPayload(entries);
    }

    @SuppressWarnings("unchecked")
    private static <T> void writeList(FriendlyByteBuf buf, DataEntry.MultiEntry<T> entry, List<?> values)
    {
        StreamCodec<T> codec = (StreamCodec<T>) DataEntry.MultiEntry.STREAM_CODECS.get(entry);

        buf.writeVarInt(values.size());

        for (Object value : values)
        {
            codec.encode(buf, (T) value);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> readList(FriendlyByteBuf buf, DataEntry.MultiEntry<T> entry)
    {
        StreamCodec<T> codec =
                (StreamCodec<T>)
                        DataEntry.MultiEntry.STREAM_CODECS.get(entry);

        int size = buf.readVarInt();

        List<T> values = new ArrayList<>(size);

        for (int i = 0; i < size; i++)
        {
            values.add(codec.decode(buf));
        }

        return values;
    }
}