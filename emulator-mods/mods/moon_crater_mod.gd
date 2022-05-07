extends Reference

var mod_name: String = "moon"

func init(global) -> void:
	print("Moon mod!")
	
	global.register_environment("moon_crater/Moon_crater", load("res://src/environments/moon_crater/Moon_crater.tscn"))
