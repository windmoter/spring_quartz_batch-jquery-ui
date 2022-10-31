 

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<head>
<title>  Schedule</title>
<script src="js/jquery.js"></script>
<script src="js/bootstrap.bundle.min.js"></script>
<script src="js/mailconfig.js"></script>
<script src="js/datatables.min.js"></script>
<script src="js/msCron.js"></script>

<link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
<link rel="stylesheet" href="js/bootstrap.min.css" />
<link rel="stylesheet" href="js/dataTables.bootstrap.min.css" />
<script type="text/javascript">
	var crontabs = [];
	var routes = [];
	$(
			function() {
				// initialize tooltips
				$('[data-toggle="tooltip"]').tooltip();
				crontabs = JSON.parse('[]');
				routes = JSON
						.parse('{"root":"/","save":"/save","run":"/runjob","crontab":"/crontab","stop":"/stop","start":"/start","remove":"/remove","backup":"/backup","restore":"/restore","delete_backup":"/delete","restore_backup":"/restore_backup","export":"/export","import":"/import","import_crontab":"/import_crontab","logger":"/logger"}');
				$("#env_vars").val(``);
			})
</script>
</head>
<body>
	<nav class="navbar navbar-light bg-light">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<a class="navbar-brand" href="#">  Schedule</a>
			</div>
		</div>
	</nav>
    <br /> 
	<div class="container" style="max-width: 1600px">
		<h3>Cron Jobs</h3>
		<br />
		<a class="btn btn-outline-primary" onclick="newJob();">
		      <span class="fa fa-plus-circle" aria-hidden="true"></span> New</a>
		<br />
		<br />
		<table class="table table-striped"> <!-- id="main_table" -->
			<thead>
				<tr>
					<th>#</th>
					<th>Schedule No</th>
					<th>Job</th>
                    <th>Schedule</th>
                    <th>Parameter</th>
					<th>Last Modified</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
            <c:forEach items="${schedules}" var="schedule" varStatus="loop">
	            <tr role="row" class="odd">
	                <td >${loop.count}.</td>
	                <td><c:out value="${schedule.batchScheNo}" /></td>
					<td><c:out value="${schedule.batchWorkNo}" /></td>
	                <td ><span style="cursor:pointer" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="<c:out value="${schedule.rmk}" />"><c:out value="${schedule.cronExpression}" /></span></td>
	                <td ><c:out value="${schedule.batchJobParam}" /></td>
	                <td  style="width:15%" title=""><c:out value="${schedule.upDt}" /></td>
	                <td  align="right">
	                   <c:choose>
		                   <c:when test="${empty runnings[schedule.batchWorkNo]}">
	    	                  <a class="btn btn-outline-info"  style="display:visible" onclick="runJob('<c:out value="${schedule.batchWorkNo}" />', this)" data-batch-job-param='<c:out value="${schedule.batchJobParam}" />'><span class="fa fa-play fa-lg" aria-hidden="true"></span> Run</a>
	    	                  <a class="btn btn-outline-secondary" style="display:none" onclick="stopJob('<c:out value="${schedule.batchWorkNo}" />')"><span class="fa fa-stop fa-lg" aria-hidden="true"></span> Stop</a>
		                   </c:when>
		                   <c:otherwise>
		                      <a class="btn btn-outline-info" style="display:none" onclick="runJob('<c:out value="${schedule.batchWorkNo}" />', this)" data-batch-job-param='<c:out value="${schedule.batchJobParam}" />'><span class="fa fa-play fa-lg" aria-hidden="true"></span> Run</a>
                              <a class="btn btn-outline-secondary" style="display:visible" onclick="stopJob('<c:out value="${schedule.batchWorkNo}" />')"><span class="fa fa-stop fa-lg" aria-hidden="true"></span> Stop</a>
		                   </c:otherwise>
	                   </c:choose>
					   <a class="btn btn-outline-warning" onclick="modifySchedule('<c:out value="${schedule.batchScheNo}" />', '<c:out value="${schedule.batchWorkNo}" />', '<c:out value="${schedule.description}" />', '<c:out value="${schedule.cronExpression}" />', this)" data-batch-job-param='<c:out value="${schedule.batchJobParam}" />' ><span class="fa fa-pencil fa-lg" aria-hidden="true"></span> Modify</a>
	                   <a class="btn btn-outline-danger" onclick="deleteJob('<c:out value="${schedule.batchScheNo}" />')"><span class="fa fa-trash-o fa-lg" aria-hidden="true"></span></a>
	                </td>
	            </tr>
	            <th colspan="7"><c:if test="${!empty schedule.description}">-<c:out escapeXml="false" value="${schedule.description}" /></c:if></th><!-- BTOCSITE-7195 -->
	       </c:forEach>
			</tbody>
		</table>
	</div>

	<!-- Job Modal -->
	<div class="modal fade" id="job">
		<div class="modal-dialog" style="max-width: 590px">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="job-title">Job</h4>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body" id="job-body">
					<label>Job Name</label>
					<select id="jobList" class="form-control browser-default custom-select" >
						<option value="NONE" label="--- JOBS ---" />
						<c:forEach items="${jobs.keySet()}" var="key">
							<option value="<c:out value="${key}" />"><c:out value="${jobs[key]}" /></option>
						</c:forEach>
					</select>
					<br />
                    <br />
                    <!-- // BTOCSITE-7195 start -->
                    <label>Job Description</label>
                    <textarea escapeXml="false" class='form-control' id='job-description' placeholder='Description'
                    style="height: 150px;"></textarea><br />
					<!-- <input type='text' class='form-control' id='job-description' placeholder='Description' /><br /> -->
					<!-- // BTOCSITE-7195 end -->
					<label>Parameter Value</label>
					<input type='text' class='form-control' id='job-param' placeholder='"ParamName1"="value1", "ParamName2"="value2"' /><br />
					<label>Schedule Type</label><br />
					<div id="cron"></div>
					<div class="row">
						<div class="col-md-2">
							<a class="btn btn-outline-primary" onclick="set_cron();"><span class="fa fa-pencil-square-o" aria-hidden="true"></span>Set</a>
						</div>
					</div>
					<br />
                    <br />
					<label>Job</label> 
					<input type='text' class='form-control' id='job-string' disabled='disabled' /><br />
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-outline-default" data-dismiss="modal"><span  class="fa fa-times" aria-hidden="true"></span>Cancel</button>
					<button type="button" class="btn btn-outline-primary" data-dismiss="modal" id="job-save"><span class="fa fa-check" aria-hidden="true"></span>Save</button>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="popup">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="modal-title">Message</h4>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body" id="modal-body"></div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal"
						id="modal-close-button">Close</button>
					<button type="button" class="btn btn-primary" data-dismiss="modal"
						id="modal-button">Ok</button>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="info-popup">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="info-title">Message</h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body" id="info-body"></div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" data-dismiss="modal"
						id="info-button">Ok</button>
				</div>
			</div>
		</div>
	</div>

	<script>
		jQuery(function($) {
			$('#main_table').DataTable({
				order : [ 1, 'asc' ],
				columns : [ {
					orderable : false
				}, null, null, null, {
					orderable : false
				}, {
					orderable : false
				} ]
			});
		});
		
		var cron = $("#cron").msCron();

		var command;
		$(document).ready(function() {
            $("#jobList").change(function() {
                //debugger;
                //alert("You have selected the country - " + selectedCountry);
                if($("#jobList option:selected").index() > 0) {
                	command = $(this).children("option:selected").val();
                }
            });
            doPoll();
        });
		
		$('#cron li a').click(function(event) {
			$("#job-string").val("");
	    });

		var set_cron = function() {
			cron = $.fn.msCron();
			console.log(cron.activeTab.toLowerCase());
			$("#job-string").val(cron.getCron);

		}

		function job_string() {
			$("#job-string").val(schedule + " " + job_command);
			return schedule + " " + job_command;
		}

		function newJob() {
			job_command = "";

			$("#jobList").removeAttr("disabled");
			$("#jobList").val("NONE");
			$("#job-string").val("");
			$("#job-description").val(""); //BTOCSITE-7195

			$("#job-minute").val("*");
			$("#job-hour").val("*");
			$("#job-day").val("*");
			$("#job-month").val("*");
			$("#job-week").val("*");

			$("#job").modal("show");
			$("#job-name").val("");
			$("#job-command").val("");
			$("#job-mailing").attr("data-json", "{}");
			
			$("#job-save").unbind("click"); // remove existing events attached to this
			$("#job-save").click(function() {

			    let param = $("#job-param").val();
				let schedule = $("#job-string").val();
				let description = $("#job-description").val(); //BTOCSITE-7195
				//debugger;
		
				var validParam = param.replace(/=/gi,":");
				validParam = "{" + validParam + "}";
				
				if(command === undefined) {
					messageBox("<p> Select JOB Name </p>", "Confirm Job", null, null, function() {
					    $("#job").modal("show");
		            });
				}
				else if(!schedule) {
					messageBox("<p> Input Schedule Value </p>", "Confirm Job", null, null, function() {
                        $("#job").modal("show");
                    });
				}
				else if( !isJsonString(validParam) ) {
					messageBox("<p> Input Parameter Value </p>", "Confirm Job", null, null, function() {
						$("#job").modal("show");
					});
					return;
				}
				else {
				    // let name = $("#job-name").val();
				    // let mailing = JSON.parse($("#job-mailing").attr("data-json"));
				    // let logging = $("#job-logging").prop("checked");
					let type = "@" + cron.activeTab.toLowerCase();
	
					//parameters:{command=test, schedule=* * * * *, _id=-1}
					$.post('schedule/save', {batchWorkNo : command, cronExpression : schedule, batchJobParam : param, execPrdTp : type, batchJobParam : param, description : description}, function(returnedData) { // BTOCSITE-7195
						console.log(returnedData);
						location.reload();
					}).fail(function() {
						console.log("error");
					});
				}
			});
		}

		function modifySchedule(_scheduleid, _jobid, description, _cronExp, $this) {
			//BTOCSITE-7195 start
			var descStr = description.replace(/<br>/gi,'\n');
			console.log("description : " + description);
			console.log("descStr : " + descStr);
			//BTOCSITE-7195 end
			job_command = "";

			$("#jobList").val(_jobid);
			$("#jobList").attr("disabled", "disabled");
			$("#job-param").val($($this).data("batchJobParam"));
			$("#job-string").val(_cronExp);
			$("#job-description").val(descStr); //BTOCSITE-7195

			$("#job").modal("show");

			//job_string();
			$("#job-save").unbind("click"); // remove existing events attached to this
			$("#job-save").click(function() {

				$("#jobList").removeAttr("disabled");

				let param = $("#job-param").val();
				let schedule = $("#job-string").val();
				let description = $("#job-description").val(); //BTOCSITE-7195
				//debugger;
				var validParam = param.replace(/=/gi,":");
				validParam = "{" + validParam + "}";
				if ( !isJsonString(validParam) ) {
					messageBox("<p> Input Parameter Value </p>", "Confirm Job", null, null, function() {
						$("#job").modal("show");
					});
					return;
				} 

				if(!schedule) {
					messageBox("<p> Input Schedule Value </p>", "Confirm Job", null, null, function() {
						$("#job").modal("show");
					});
				}
				else {
					// let name = $("#job-name").val();
					// let mailing = JSON.parse($("#job-mailing").attr("data-json"));
					// let logging = $("#job-logging").prop("checked");
					let type = "@" + cron.activeTab.toLowerCase();

					//parameters:{command=test, schedule=* * * * *, _id=-1}
					$.post('job/modify', {
						batchScheNo : _scheduleid
					  , batchWorkNo: _jobid
					  , cronExpression : schedule
					  , batchJobParam : param
					  , execPrdTp : type
					  , description : description // BTOCSITE-7195
					}, function(returnedData) {
						console.log(returnedData);
						location.reload();
					}).fail(function() {
						console.log("error");
					});
				}
			});
		}

		function isJsonString(str) {
			try {
				if ( typeof str !== "string" ) {
					return false;
				}
			    var json = JSON.parse(str);
			  } catch (e) {
			    return false;
			  }
			return true;
		}
		
		function deleteJob(_id) {
		    messageBox("<p> Do you want to delete this Schedule? </p>", "Confirm delete", null, null, function() {
		       /*  $.post(routes.remove, {_id: _id}, function(){
		            location.reload();
		        }); */
		        console.log(_id);
		        $.ajax({
		            url: 'schedule/delete/'+'?'+$.param({"batchWorkNo" : _id}),
		            type: 'DELETE',
		           // data: {"batchWorkNo" : _id},
		            success: function(result) {
		            	location.reload();
		            }
		        });
		    });
		}

		function stopJob(_id) {
		    messageBox("<p> Do you want to stop this Job? </p>", "Confirm stop job", null, null, function() {
		        $.post('job/stop/'+_id, {_id: _id}, function() {
		            location.reload();
		        });
		    });
		}

		function runJob(_id, $this) {
            var param = $($this).data("batchJobParam")
            param = param.replace(/=/gi,":");
            param = "{" + param + "}";
	        $.ajax({
	            url: 'job/run/'+_id,
                type: 'POST',
                contentType: "application/json",
                // data: '{jobParameters : '+JSON.stringify(parameter)+'}',
                data:  JSON.stringify({ "jobParameters": JSON.parse(param) }),
                success: function(result) {
                    infoMessageBox("JOB Called : "+JSON.parse(result).STATUS, "Run job");
                 }
            }); 
		}
		
		function doPoll() {
		    $.post('runningjobs', function(data) {
		    	$(".table").find('tr').each(function (i, el) {
		            var $row = $(el);
	                if(data[$row.find('td').eq(2).text()] === undefined) {
	                	$row.find('td').eq(6).find('a').eq(0).show();
	                	$row.find('td').eq(6).find('a').eq(1).hide();
	                } else {
	                	$row.find('td').eq(6).find('a').eq(0).hide();
                        $row.find('td').eq(6).find('a').eq(1).show();
		            }
		        });
		        setTimeout(doPoll, 3000);
		    });
		}
		
		/***** MessageBox *****/
		// simply show info, only close button
		function infoMessageBox(message, title) {
		    $("#info-body").html(message);
		    $("#info-title").html(title);
		    $("#info-popup").modal('show');
		}
		// like info, but for errors
		function errorMessageBox(message) {
		    var msg = "Operation failed: " + message + ". " + "Please see error log for details.";
		    infoMessageBox(msg, "Error");
		}
		// modal with full control
		function messageBox(body, title, ok_text, close_text, callback) {
		    $("#modal-body").html(body);
		    $("#modal-title").html(title);
		    if (ok_text) {
		    	$("#modal-button").html(ok_text);
		    }
		    if(close_text) {
		    	$("#modal-close-button").html(close_text);
			}
		    $("#modal-button").unbind("click"); // remove existing events attached to this
		    $("#modal-button").click(callback);
		    $("#popup").modal("show");
		}
	</script>
</body>
</html>
