package cn.advu.workflow.web.facade.workflow.vo;

public class WorkflowVO {

//	private File file;		//流程定义部署文件
	private String flowName;//流程定义名称

	private String id;//申请单ID
	
	private String deploymentId;//部署对象ID
	private String imageName;	//资源文件名称
	private String taskId;		//任务ID
	private String outcome;		//连线名称
	private String comment;		//备注

//	public File getFile() {
//		return file;
//	}
//
//	public void setFile(File file) {
//		this.file = file;
//	}

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
