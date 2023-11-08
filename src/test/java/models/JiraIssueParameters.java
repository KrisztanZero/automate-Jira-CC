package models;

import java.util.List;

public class JiraIssueParameters {
    private String projectName;
    private String issueType;
    private String summary;
    private String attachmentPath;
    private String dueDate;
    private String description;
    private String assignee;
    private String priority;
    private List<String> labels;
    private String originalEstimate;
    private String remainingEstimate;

    public JiraIssueParameters(String projectName, String issueType, String summary, String attachmentPath, String dueDate, String description, String assignee, String priority, List<String> labels, String originalEstimate, String remainingEstimate) {
        this.projectName = projectName;
        this.issueType = issueType;
        this.summary = summary;
        this.attachmentPath = attachmentPath;
        this.dueDate = dueDate;
        this.description = description;
        this.assignee = assignee;
        this.priority = priority;
        this.labels = labels;
        this.originalEstimate = originalEstimate;
        this.remainingEstimate = remainingEstimate;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getIssueType() {
        return issueType;
    }

    public String getSummary() {
        return summary;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getDescription() {
        return description;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getPriority() {
        return priority;
    }

    public List<String> getLabels() {
        return labels;
    }

    public String getOriginalEstimate() {
        return originalEstimate;
    }

    public String getRemainingEstimate() {
        return remainingEstimate;
    }
}
