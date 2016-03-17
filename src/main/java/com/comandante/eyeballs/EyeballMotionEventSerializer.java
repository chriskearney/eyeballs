package com.comandante.eyeballs;

import com.google.gson.GsonBuilder;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class EyeballMotionEventSerializer implements Serializer<EyeballMotionEvent>, Serializable {

    @Override
    public int fixedSize() {
        return -1;
    }

    @Override
    public void serialize(DataOutput dataOutput, EyeballMotionEvent eyeballMotionEvent) throws IOException {
        dataOutput.writeUTF(new GsonBuilder().create().toJson(eyeballMotionEvent, EyeballMotionEvent.class));

    }

    @Override
    public EyeballMotionEvent deserialize(DataInput dataInput, int i) throws IOException {
        return new GsonBuilder().create().fromJson(dataInput.readUTF(), EyeballMotionEvent.class);
    }

}
