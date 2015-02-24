<?php
$servername = "mysql.vhostfull.com";
$username = "u928696073_gmap";
$password = "ws206771";
$dbname = "u928696073_gmap";
$host=$_GET['host'];
$group=$_GET['group'];

//tbl_p2p(hostname,sdp,create_time,last_update_time,group_id)
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$sql = "truncate table tbl_p2p " ;
$result = $conn->query($sql);
if ( $result === TRUE ) {
	echo "\n truncated\n";
} else {
	echo "error: " . $sql . "<br>" . $conn->error;
}
$conn->close();
?>