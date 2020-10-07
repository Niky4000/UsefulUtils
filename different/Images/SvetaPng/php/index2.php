<?php
$ok="Ok!";
$noData="No Data!";
if($_REQUEST["getFileList"]!=null){
	print_r(scandir("data"));
}
else if($_REQUEST["putRequest"]!=null&&$_REQUEST["cpuId"]!=null&&$_REQUEST["text"]!=null&&$_REQUEST["subject"]!=null){
	file_put_contents("data/".$_REQUEST["cpuId"]."_request","cpuId=".$_REQUEST["cpuId"]."&"."text=".$_REQUEST["text"]."&"."subject=".$_REQUEST["subject"]);
	echo $ok;
}
else if($_REQUEST["putResponse"]!=null&&$_REQUEST["cpuId"]!=null&&$_REQUEST["text"]!=null&&$_REQUEST["subject"]!=null){
	file_put_contents("data/".$_REQUEST["cpuId"]."_response","cpuId=".$_REQUEST["cpuId"]."&"."text=".$_REQUEST["text"]."&"."subject=".$_REQUEST["subject"]);
	echo $ok;
}
else if($_REQUEST["getRequest"]!=null&&$_REQUEST["cpuId"]!=null){
	if(file_exists("data/".$_REQUEST["cpuId"]."_request")){
		echo file_get_contents("data/".$_REQUEST["cpuId"]."_request");
	}
	else{
		echo $noData;
	}
}
else if($_REQUEST["getResponse"]!=null&&$_REQUEST["cpuId"]!=null){
	if(file_exists("data/".$_REQUEST["cpuId"]."_response")){
		echo file_get_contents("data/".$_REQUEST["cpuId"]."_response");
	}
	else{
		echo $noData;
	}
}
else if($_REQUEST["deleteRequest"]!=null&&$_REQUEST["cpuId"]!=null){
	if(file_exists("data/".$_REQUEST["cpuId"]."_request")){
		unlink("data/".$_REQUEST["cpuId"]."_request");
		echo $ok;
	}
	else{
		echo $noData;
	}
}
else if($_REQUEST["deleteResponse"]!=null&&$_REQUEST["cpuId"]!=null){
	if(file_exists("data/".$_REQUEST["cpuId"]."_response")){
		unlink("data/".$_REQUEST["cpuId"]."_response");
		echo $ok;
	}
	else{
		echo $noData;
	}
}
else{
	echo $noData;
}
?>

