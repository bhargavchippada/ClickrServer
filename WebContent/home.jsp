<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<link rel="stylesheet" type="text/css"
	href="./bootstrap-dist/css/bootstrap.min.css">
<title>Home Page</title>
<style>
body {
	background-color: #ffffff;
	overflow-x: hidden;
	overflow-y: auto;
}

.option-style {
	width: 100%;
	margin-bottom: 8px;
}

.full-width {
	width: 100%;
}

.question-style {
	margin-bottom: 8px;
}

.questioncontainer {
	position: relative;
	margin-top: 48px;
	margin-bottom: 72px;
	margin-left: 25%;
	width: 50%;
}

.form-heading {
	font-size: 36px;
	margin-bottom: 12px;
}

.buttoncls {
	position: relative;
	width: 144px;
}

.logoimage {
	width: 48px;
	height: 48px;
	margin-bottom: 8px;
}

.trackingcontainer {
	position: relative;
	margin-top: 32px;
	margin-left: 20%;
	width: 60%;
}

.darkline {
	position: relative;
	background-color: #333333;
	margin-top: 32px;
	height: 1px;
	margin-left: 20%;
	width: 60%;
}
</style>
<!--<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>-->
<script type="text/javascript" src="./bootstrap-dist/jquery.js"></script>
</head>

<body>
	<div class="questioncontainer">
		<p class="form-heading">
			<img src="./media/inverted_launcher.png" class="logoimage">
			Clicker
			<button class="buttoncls btn btn-success btn-lg" type="submit"
				id="server_state" value="0">Start Server</button>
		</p>
		<p class="form-heading">Question:</p>
		<form role="form" action="" id="question-form">
			<textarea type="text" rows="5" placeholder="Type Question"
				class="form-control question-style" name="questiontext"
				aria-label="..."></textarea>
			<div id="question-options" class="btn-group-vertical full-width">
				<div class="input-group option-style">
					<span class="input-group-addon"> <input type="radio"
						name="options-group" value="0" aria-label="...">
					</span> <input type="text" class="form-control" name="option0"
						aria-label="..."> <span
						class="input-group-btn remove-option">
						<button class="btn btn-default" type="button">
							<b>X</b>
						</button>
					</span>
				</div>
			</div>
			<button class="btn add-option btn-default pull-left">
				<span class="glyphicon glyphicon-plus" aria-hidden="true">
					Add new option...</span>
			</button>
			<button class="buttoncls btn btn-success btn-lg"
				style="margin-left: 16px; margin-top: 48px;" type="submit"
				name="submitquiz" value="1">Save Quiz</button>
		</form>

		<form role="form" action="">
			<button class="buttoncls btn btn-success btn-lg pull-right"
				style="margin-top: -46px;" type="submit" name="quizstatus"
				value="start" onclick="quizToggle()">Start Quiz</button>
		</form>
	</div>
	<div class="darkline"></div>
	<div class="trackingcontainer">
		<p class="form-heading">Students:</p>
		<!-- Table -->
		<table class="table">
			<tr>
				<th>Roll-number</th>
				<th>Name</th>
				<th>Answer</th>
				<th>Status</th>
				<th>Last-Update</th>
			</tr>
			<tr>
				<td>120050053</td>
				<td>Bhargav</td>
				<td>1</td>
				<td><span class="label label-success">Connected</span></td>
				<td>17:13 PM</td>
			</tr>
			<tr>
				<td>120050053</td>
				<td>Bhargav</td>
				<td>1</td>
				<td>Connected</td>
				<td>17:13 PM</td>
			</tr>
		</table>
	</div>
	<div class="darkline"></div>
	<div class="trackingcontainer">
		<div class="progress">
			<div class="progress-bar progress-bar-success" role="progressbar"
				aria-valuenow="40" aria-valuemin="0" aria-valuemax="100"
				style="width: 40%">40% Complete (success)</div>
		</div>
		<div class="progress">
			<div class="progress-bar progress-bar-info" role="progressbar"
				aria-valuenow="20" aria-valuemin="0" aria-valuemax="100"
				style="width: 20%">20% Complete</div>
		</div>
		<div class="progress">
			<div class="progress-bar progress-bar-warning" role="progressbar"
				aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"
				style="width: 60%">60% Complete (warning)</div>
		</div>
		<div class="progress">
			<div class="progress-bar progress-bar-danger" role="progressbar"
				aria-valuenow="80" aria-valuemin="0" aria-valuemax="100"
				style="width: 80%">80% Complete (danger)</div>
		</div>
	</div>
</body>

<script type="text/javascript">

      String.format = function() {
        var s = arguments[0];
        for (var i = 0; i < arguments.length - 1; i++) {
            var reg = new RegExp("\\{" + i + "\\}", "gm");
            s = s.replace(reg, arguments[i + 1]);
        }

        return s;
      }
      var option_html = '<div class="input-group option-style">'+
                          '<span class="input-group-addon">'+
                            '<input type="radio" name="options-group" value="{0}" aria-label="...">'+
                          "</span>"+
                          '<input type="text" class="form-control" name="option{1}" aria-label="...">'+
                          '<span class="input-group-btn remove-option">'+
                            '<button class="btn btn-default" type="button"><b>X</b></button>'+
                          "</span>"+
                          "</div>",
      options_index = 0;

      (function( $ ) {
        $(function() {
            $('.add-option').click(function() {
                addOption();
            });

            $('.remove-option').click(function() {
                $(this).parent().remove();
            });

            $('#server_state').on('click',function() {
              console.log ( '#server_state was clicked' );
              var button = $('#server_state');
              if(button.attr("value")=="0"){
            	changeButtonState('#server_state','btn-success','btn-danger','1','Stop Server');
            	console.log ( 'server has started' );
              }else{
            	changeButtonState('#server_state','btn-danger','btn-success','0','Start Server');
            	console.log ( 'server has stopped' );
              }
             });
        });
      })( jQuery );
	
      function changeButtonState(){
    	  var button = $(arguments[0]);
    	  button.removeClass(arguments[1]);
    	  button.addClass(arguments[2]);
    	  button.attr("value",arguments[3]);
    	  button.html(arguments[4]);
      };
      
      function addOption() {
          $('#question-options').append(String.format(option_html, options_index, options_index));
          $('.remove-option').click(function() {
              $(this).parent().remove();
          })
          options_index = options_index + 1;
      }

      function quizToggle(){
         document.getElementById("quiztoggle").value = "end";
         document.getElementById("quiztoggle").textContent = "End Quiz";
      }
    </script>
</html>
