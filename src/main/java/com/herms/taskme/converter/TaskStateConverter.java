package com.herms.taskme.converter;

import com.herms.taskme.enums.TaskState;

import javax.persistence.AttributeConverter;

public class TaskStateConverter implements AttributeConverter<TaskState, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TaskState attribute) {
        if(attribute != null) {
            return attribute.getCode();
        }
        return null;
    }

    @Override
    public TaskState convertToEntityAttribute(Integer dbData) {
        if(dbData != null) {
            return TaskState.toEnum(dbData);
        }
        return null;
    }
}
