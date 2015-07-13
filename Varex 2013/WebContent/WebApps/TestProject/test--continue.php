<?php

	$a = array(0 => __CHOICE__("CAL", "A", ""));

	foreach ( $a as $val ) {
		if ( empty($val) ) {
			echo "hello";
			continue;
		}
		echo "hi";
	}

?>