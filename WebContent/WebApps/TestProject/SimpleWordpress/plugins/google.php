<?php {
	
	function google_func() {
		global $footer;
		$footer = "Running Google plugin...";
	}
	
	add_action("footer_hook", google_func);
	
} ?>