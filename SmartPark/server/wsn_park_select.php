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

$lat=$_GET['lat'];
$lng=$_GET['lng'];

// wsn_parking_space_info (id,status,lat,lng,operator,type,admin,country,operator,install_time,comment)
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
//1 lat = 110.57 km, 1 lng = 111.32 km
//1km = 0.009 lat = 0.009 lng (0.01)
//diff/0.00001=m
$comment='floor(SQRT(POW('.$lat.'-lat,2)+POW('.$lng.'-lng,2))*100000) as comment';
$select = "SELECT id,status,lat,lng,operator,type,admin,country,".$comment." FROM wsn_parking_space_info";
$where = " where lat between ". ($lat -0.01) ." and ". ($lat +0.01) ." and lng between ". ($lng-0.01) ." and ". ($lng+0.01);
$sql=$select.$where;
$result = $conn->query($sql);
if ($result->num_rows > 0) {
	echo '{ "results" : [' . "\r\n";
	while($row = $result->fetch_assoc()) {
		echo ( json_encode($row) . ",\r\n");
	}
	echo "]}\r\n";
} else {
    echo "no results:sql=". $sql;
}
$conn->close();
?>