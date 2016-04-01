package com.comandante.eyeballs.model;

import com.google.gson.GsonBuilder;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class LocalEventSerializer implements Serializer<MotionEvent>, Serializable {

    @Override
    public int fixedSize() {
        return -1;
    }

    @Override
    public void serialize(DataOutput dataOutput, MotionEvent motionEvent) throws IOException {
        dataOutput.writeUTF(new GsonBuilder().create().toJson(motionEvent, MotionEvent.class));
    }

    @Override
    public MotionEvent deserialize(DataInput dataInput, int i) throws IOException {
        return new GsonBuilder().create().fromJson(dataInput.readUTF(), MotionEvent.class);
    }

}
