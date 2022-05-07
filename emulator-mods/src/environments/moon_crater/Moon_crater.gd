extends Spatial

func init_cam_pos() -> Transform:
	return $Camera.global_transform


func get_spawn_position(hint: String) -> Transform:
	return $Vehicle.global_transform
