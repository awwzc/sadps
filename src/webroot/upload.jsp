<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%-- <%@ page isELIgnored="false" %> --%>
<!DOCTYPE html>
<html lang="zh-CN" style="background: none;">

<head>
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/user-center.css">
    <link rel="stylesheet" href="css/complaint.css">
    <script src="js/jquery.js"></script>
    <style type="text/css">
    body {
        min-width: 785px;
    }
    .btn {
        margin-top: 15px;
    }
    .file {
        width: 550px;
        )
    </style>
    <script type="text/javascript">
    var projects = '<c:out value="${serverInfo}"/>';
    $(function() {
        $("#file-add-btn").click(function() {
            $(".file-add").append('<input type="file" name="fileName" class="file">');
        });
        $("#reboot-btn").click(function() {
        	$("#reboot-debug-action").val("reboot");        	
        	$("#reboot-debug-form").submit();        	
        });
        $("#debug-btn").click(function() {
        	$("#reboot-debug-action").val("debug");        	
        	$("#reboot-debug-form").submit();        	
        });
        if (projects) {
            var projectArr = projects.split(",");
            $.each(projectArr, function(index, val) {
                $("#debug-project").append('<label><input type="checkbox" name="project" value="' + val + '" >' + val + '</label>&nbsp;');
                $("#project-select").append('<label><input type="checkbox" name="project" value="' + val + '" >' + val + '</label>&nbsp;');
            });
        }
    });
    </script>
</head>

<body>

    <form class="complaint-form" action="work.do" method="post" data-provide="validation-1.0" enctype="multipart/form-data">
    	<input type="hidden" name="action" value="upload">
    	<input type="hidden" name="serverInfo" value="<c:out value="${serverInfo}"/>">
        <div class="tips-title" style="margin-top: 4px;">
            <i class="icon icon-info"></i> 
            <span class="title">开发联调环境自助部署
                <c:if test="${!empty serverInfo}">,当前服务器为
                    <c:out value="${serverInfo}" /></c:if>
            </span>
        </div>

        <div class="content-panel something">
            <i class="arrow-content-panel"><i></i>
            </i>
            <div class="control-group">
                <label class="control-label">
                    部署选项：
                </label>
                <div class="controls">
                    <div class="complaint-tags">
                        <label>
                            <input type="checkbox" name="autoReboot" value="true" checked=checked>自动重启
                        </label>
                    </div>
                </div>
            </div>
            <c:if test="${!empty serverInfo}">
                <div class="control-group">
                    <label class="control-label">
                        debug工程：
                    </label>
                    <div class="controls">
                        <div class="complaint-tags" id="debug-project">
                        </div>
                    </div>
                </div>
            </c:if>

            <div class="control-group">
                <label class="control-label">
                    <em class="required-mark">*</em>上传war包：</label>
                <div class="controls">
                    <div class="complaint-files">
                        <div class="file-upload">
                            <ul class="file-list">
                                <li class="file-add" data-check-url="/mem/complaint.do?action=allowUpload" data-url="/mem/complaint.do?action=upload" data-delete-url="/mem/complaint.do?action=fileDelete" data-hidden-name="fileName">
                                    <input type="file" name="fileName" class="file">
                                </li>
                                <input type="button" id="file-add-btn" class="btn" value="添加">
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">配置文件：</label>
                <div class="controls">
                    <div class="complaint-descript">
                        <placeholder class="placeholder unselect" style="overflow: hidden; white-space: nowrap; cursor: text; margin: 0px; font-size: 12px; font-style: normal; font-weight: normal; font-family: Tahoma, Arial, &#39;Hiragino Sans GB&#39;, 宋体, sans-serif; line-height: 17.1420001983643px; position: absolute; left: 1px; top: 1px; max-width: 635px; padding: 8px 6px; display: inline;">建议：
                            <br>1、请按照Java properties文件格式
                            <br>2、已存在key值将做更新处理</placeholder>
                        <textarea name="content" class="descript-textarea" maxlength="500"></textarea>
                    </div>
                </div>
            </div>
        </div>

        <div class="submit-btn-wrapper" style="margin: 0px 0px;">
            <span class="ui-progress-btn-wrapper" style="width: 176px; margin: 0px 10px 0px 0px; border-radius: 2px; vertical-align: middle;">
                <span class="ui-progress-btn-bar" style="border-radius: 2px 0px 0px 2px;"></span>
                <input type="submit" class="btn btn-submit ui-progress-btn-wrapped ui-progress-btn"  value="上传文件">
            </span>
        </div>
    </form>

    <form class="complaint-form" id="reboot-debug-form" action="work.do" method="post" data-provide="validation-1.0">
    	<input type="hidden" id="reboot-debug-action" name="action" value="">
        <div class="tips-title">
            <i class="icon icon-info"></i> 
            <span class="title">开发联调环境重启、Debug
                <c:if test="${!empty serverInfo}">,当前服务器为
                    <c:out value="${serverInfo}" /></c:if>
            </span>
        </div>

        <div class="content-panel something">
            <i class="arrow-content-panel"><i></i>
            </i>
            <c:if test="${!empty serverInfo}">
                <div class="control-group">
                    <label class="control-label">
                        请选择工程：
                    </label>
                    <div class="controls">
                        <div class="complaint-tags" id="project-select">
                        </div>
                    </div>
                </div>
            </c:if>

        </div>

        <div style="margin-left:260px;margin-bottom: 20px;">
            <button class="btn btn-primary" id="reboot-btn">
                <i class="glyphicon glyphicon-refresh"></i>
                重启服务器
            </button>
            <button class="btn btn-primary" id="debug-btn">
                <i class="glyphicon glyphicon-refresh"></i>
                开启Debug模式
            </button>
        </div>
    </form>
</body>
