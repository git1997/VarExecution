<?php
	$plugins = array();
	
	// INST ADDED BY HUNG
	if (__INSTRUMENT__) {
		$active_plugins = array(
			__CHOICE__("CAL", "my-calendar/my-calendar.php", ""),
			__CHOICE__("WEA", "weather-and-weather-forecast-widget/weather_widget.php", "")
		);
		print_r($active_plugins);
	}
	// END OF ADDED CODE

	if ( empty( $active_plugins ) || defined( 'WP_INSTALLING' ) )
		return $plugins;

	foreach ( $active_plugins as $plugin ) {
	
		// INST ADDED BY HUNG
		if (__INSTRUMENT__) {
			echo $plugin;
			echo "[2]" . ('.php' == substr( $plugin, -4 ));
		}
		// END OF ADDED CODE
	
		if ( 
			'.php' == substr( $plugin, -4 ) // $plugin must end with '.php'
			)
		$plugins[] = WP_PLUGIN_DIR . '/' . $plugin;
	}
	
	// INST ADDED BY HUNG
	if (__INSTRUMENT__) {
		print_r($plugins);
	}
	// END OF ADDED CODE
?>