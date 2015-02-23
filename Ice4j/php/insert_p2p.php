<?php
$servername = "mysql.vhostfull.com";
$username = "u928696073_gmap";
$password = "ws206771";
$dbname = "u928696073_gmap";
$host=$_POST['host'];
$sdp=$_POST['sdp'];
$group=$_POST['group'];

//tbl_p2p(hostname,sdp,create_time,last_update_time,group_id)
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
//TODO remove all records before adding self sdp
$sql = "truncate table tbl_p2p" ;
if ($result->num_rows > 0) {
}
/*
$sql = "SELECT hostname FROM tbl_p2p where group_id = " . $group . " and hostname = '" . $host . "'";
$result = $conn->query($sql);
if ($result->num_rows > 0) {
	//echo "got " . $result->num_rows . "rows: " . $sql ;
	$sql = "UPDATE tbl_p2p set sdp = '" . $sdp . "', last_update_time=now(),create_time=now() where group_id = " . $group . " and hostname = '" . $host . "'";
	if ($conn->query($sql) === TRUE) {
		echo "\n" . $host . " updated\n";
	} else {
		echo "error: " . $sql . "<br>" . $conn->error;
	}
} else {
	//echo "got " . $result->num_rows . "rows: " . $sql ;
    $sql = "INSERT INTO tbl_p2p ( hostname,sdp,create_time,last_update_time,group_id ) values ('" 
		. $host . "','" . $sdp . "', now(), now()," . $group . ")";
	if ($conn->query($sql) === TRUE) {
		echo "\n" . $host . " created\n";
	} else {
		echo "error: " . $sql . "<br>" . $conn->error;
	}
}
*/
$conn->close();
?>