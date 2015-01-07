<?php
	echo phpversion();
	$version = true;
	if ($version)
		$trace = debug_backtrace( false );
	else
		$trace = debug_backtrace();
?>