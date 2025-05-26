package com.span.bulkuploadpipeline.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "goals")
public class Goal {

    @Id
    private String goalId;  // Map to goal_id in CSV

    private String goalName;
    private String goalDescription;
    private String goalAssignedToPerson;

    public Goal() {}

    public Goal(String goalId, String goalName, String goalDescription, String goalAssignedToPerson) {
        this.goalId = goalId;
        this.goalName = goalName;
        this.goalDescription = goalDescription;
        this.goalAssignedToPerson = goalAssignedToPerson;
    }

    // Getters and setters below

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public String getGoalDescription() {
        return goalDescription;
    }

    public void setGoalDescription(String goalDescription) {
        this.goalDescription = goalDescription;
    }

    public String getGoalAssignedToPerson() {
        return goalAssignedToPerson;
    }

    public void setGoalAssignedToPerson(String goalAssignedToPerson) {
        this.goalAssignedToPerson = goalAssignedToPerson;
    }
}
