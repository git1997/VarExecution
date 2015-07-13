<?php 

	if (__INSTRUMENT__) {
		$CAL = __CHOICE__("CAL");
		$WEA = __CHOICE__("WEA");
	}
	else {
		$CAL = true;
		$WEA = false;
	}
	
	if ($CAL) {
		$key = "1";
	}
	else
		$key = "4";
	
?>

<?php 
	$x = array("1" => "a", "2" =>"b");
	$x[$key] = "c"; 
	print_r($x);

?>