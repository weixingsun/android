<?php

@ini_set('max_execution_time','99999');
@ini_set('memory_limit','-1');

header("Content-type:application/vnd.ms-txt");
header("Content-Type: application/force-download");
header("Content-Type: application/octet-stream");
header("Content-Type: application/download");	
Header("Accept-Ranges: bytes");
header("Pragma: public");
header("Expires: 0");
header("Cache-Control: must-revalidate, post-check=0, pre-check=0");	
header('Content-Disposition: attachment; filename="json'.date("Y-m-d",time()).'.txt"');
header("Content-Transfer-Encoding: binary ");


$servername = "mysql.vhostfull.com";
$username = "u928696073_gmap";
$password = "ws206771";
$dbname = "u928696073_gmap";
$host = $_POST['host'];
$group = $_POST['group'];
$sdp = $_POST['sdp'];

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

//TODO make sure contains self sdp record
$sql = "SELECT hostname FROM tbl_p2p where group_id = " . $group . " and hostname = '" . $host . "'";
$result = $conn->query($sql);
if ($result->num_rows > 0) {
	//
} else {
    $sql = "INSERT INTO tbl_p2p ( hostname,sdp,create_time,last_update_time,group_id ) values ('" 
		. $host . "','" . $sdp . "', now(), now()," . $group . ")";
	if ($conn->query($sql) === TRUE) {
		//echo "\n" . $host . " created\n";
	} else {
		echo "error: " . $sql . "<br>" . $conn->error;
	}
}
//$sql = "SELECT floor((UNIX_TIMESTAMP(now())-UNIX_TIMESTAMP(report_time))/60) as report_time, FROM reminder ";
//tbl_p2p(hostname,sdp,create_time,last_update_time,group_id)
$sql = "SELECT hostname,sdp FROM tbl_p2p where last_update_time=create_time and group_id = " . $group ; // . " and hostname = '" . $host . "'";
$result = $conn->query($sql);
//$db_arr = $conn->fetch_assoc($result);
//echo ( json_encode($db_arr));

if ($result->num_rows > 0) {
    // output data of each row
	 echo '{ "id": 1,' . "\r\n" . '"result" : [' . "\r\n";
       while($row = $result->fetch_assoc()) {
       //echo  " - Type: " . $row["type"]. " - Reporter:" . $row["reporter"]. " - Comment:" . $row["comment"]. "<br>";
	   echo ( json_encode($row) . ",\r\n");//现在这样是数据库中所有的记录都会在一行上显示。
	   //echo ( json_encode($row).'<br>'); //没条记录换一行 
       }
	 echo "]\r\n}";
} else {
    echo "no results:" . $sql;
}

$conn->close();
?>