package com.pandapulsestudios.pulsecore._Common.VariableTests.JavaLang;

import com.pandapulsestudios.pulsecore.JavaAPI.Interface.PulseAutoRegister;
import com.pandapulsestudios.pulsecore.NBTAPI.Enums.PersistentDataTypes;
import com.pandapulsestudios.pulsecore.VariableAPI.Interface.PulseVariableTest;

import java.util.ArrayList;
import java.util.List;

@PulseAutoRegister
public class FloatTest implements PulseVariableTest {
    @Override
    public PersistentDataTypes PersistentDataType() { return PersistentDataTypes.FLOAT; }
    @Override
    public boolean IsType(Object variable) {
        try{
            float x = Float.parseFloat(variable.toString());
            return true;
        }catch (NumberFormatException e){ return false; }
    }

    @Override
    public List<Class<?>> ClassTypes() {
        var classTypes = new ArrayList<Class<?>>();
        classTypes.add(float.class);
        classTypes.add(Float.class);
        classTypes.add(float[].class);
        classTypes.add(Float[].class);
        return classTypes;
    }

    @Override
    public Object SerializeData(Object serializedData) {
        return serializedData;
    }

    @Override
    public Object DeSerializeData(Object serializedData) {
        try {return Float.parseFloat(serializedData.toString());}
        catch (NumberFormatException e) { return serializedData; }
    }

    @Override
    public Object ReturnDefaultValue() { return 0f; }

    @Override
    public List<String> TabData(List<String> baseTabList, String currentArgument) {
        return List.of("[FLOAT]");
    }
}
