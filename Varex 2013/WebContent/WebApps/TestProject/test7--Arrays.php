<?php

$COND1 = __CHOICE__("COND1");
$COND2 = __CHOICE__("COND2");

if ($COND1) {
	$x = array();
	$x[0] = 'A';
	$x[] = 'B';
}
else
	$x = array();

echo $x;

if (is_null($x)) {
	echo "GOOD";
}

(array) $x;
// if ($COND2) 
// 	$x[] = "B";

//echo $y;
//echo $x;
//print_r($x);
echo $x[0];
// echo count($x);

// if ($COND3)
//   $x[0] = "C";

// print_r($x);
// echo count($x);

//push($plugins, "google.php");// $plugins[1] = "google.php"; //array_push($plugins, "google.php"); //$plugins[] = "google.php";
?>