package com.wdiscute.utils.network;

import com.wdiscute.utils.DataEntry;
import com.wdiscute.utils.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.nikdo53.neobackports.io.StreamCodec;
import net.nikdo53.neobackports.io.networking.CustomPacketPayload;
import net.nikdo53.neobackports.io.networking.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record DataEntrySyncPayload(List<Map.Entry<DataEntry<?>, Object>> entries) implements CustomPacketPayload
{
    public static final Type<DataEntrySyncPayload> TYPE =
            new Type<>(Utils.rl("wdutils", "data_entry_sync"), DataEntrySyncPayload.class);

    public static final StreamCodec<DataEntrySyncPayload> STREAM_CODEC =
            StreamCodec.of(
                    DataEntrySyncPayload::encode,
                    DataEntrySyncPayload::decode
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
                DataEntry.MAP.put(entry.getKey(), entry.getValue());
            }
        });
    }

    private static void encode(
            FriendlyByteBuf buf,
            DataEntrySyncPayload payload
    )
    {
        buf.writeVarInt(payload.entries.size());

        for (var entry : payload.entries)
        {
            DataEntry<?> dataEntry = entry.getKey();

            buf.writeResourceLocation(dataEntry.rl());

            writeValue(buf, dataEntry, entry.getValue());
        }
    }

    private static DataEntrySyncPayload decode(
            FriendlyByteBuf buf
    )
    {
        int size = buf.readVarInt();
        List<Map.Entry<DataEntry<?>, Object>> entries = new ArrayList<>(size);

        for (int i = 0; i < size; i++)
        {
            var rl = buf.readResourceLocation();

            DataEntry<?> dataEntry = DataEntry.SYNC_ENTRIES_BY_ID.get(rl);

            if (dataEntry == null)
            {
                throw new IllegalStateException(
                        "Unknown DataEntry: " + rl
                );
            }

            Object value = readValue(buf, dataEntry);

            entries.add(Map.entry(dataEntry, value));
        }

        return new DataEntrySyncPayload(entries);
    }

    @SuppressWarnings("unchecked")
    private static <T> void writeValue(
            FriendlyByteBuf buf,
            DataEntry<T> entry,
            Object value
    )
    {
        StreamCodec<T> codec =
                (StreamCodec<T>)
                        DataEntry.STREAM_CODECS.get(entry);

        if (codec == null)
        {
            throw new IllegalStateException(
                    "No StreamCodec registered for: " + entry.rl()
            );
        }

        codec.encode(buf, (T) value);
    }

    @SuppressWarnings("unchecked")
    private static <T> T readValue(
            FriendlyByteBuf buf,
            DataEntry<T> entry
    )
    {
        StreamCodec<T> codec =
                (StreamCodec<T>)
                        DataEntry.STREAM_CODECS.get(entry);

        if (codec == null)
        {
            throw new IllegalStateException(
                    "No StreamCodec registered for: " + entry.rl()
            );
        }

        return codec.decode(buf);
    }
}