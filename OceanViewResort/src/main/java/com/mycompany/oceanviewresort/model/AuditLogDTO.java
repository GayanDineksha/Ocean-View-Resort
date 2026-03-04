/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oceanviewresort.model;

/**
 *
 * @author User
 */
public class AuditLogDTO {
    private long logId;
    private String username;
    private String actionType;
    private String tableName;
    private long recordId;
    private String oldValues;
    private String newValues;
    private String actionTimestamp;

    public AuditLogDTO() {}

    public long getLogId() { return logId; }
    public void setLogId(long logId) { this.logId = logId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public long getRecordId() { return recordId; }
    public void setRecordId(long recordId) { this.recordId = recordId; }

    public String getOldValues() { return oldValues; }
    public void setOldValues(String oldValues) { this.oldValues = oldValues; }

    public String getNewValues() { return newValues; }
    public void setNewValues(String newValues) { this.newValues = newValues; }

    public String getActionTimestamp() { return actionTimestamp; }
    public void setActionTimestamp(String actionTimestamp) { this.actionTimestamp = actionTimestamp; }
}
