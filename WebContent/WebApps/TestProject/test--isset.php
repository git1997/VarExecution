<?php 

	$foo = __CHOICE__("foo");
	
	if ($foo)
		$x = 1;
	
	if (isset($x))
		$msg = "x is defined";
	
	echo $msg;
	
	if (!isset($x))
		$x = 2;
	echo $x;
?>