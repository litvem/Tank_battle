extends Spatial

var locations = [Transform(Basis(), Vector3(rand_range(80,80), rand_range(5,5), rand_range(-20,-20))),
				 Transform(Basis(), Vector3(rand_range(60,60), rand_range(5,5), rand_range(40,40))),
				 Transform(Basis(), Vector3(rand_range(40,40), rand_range(5,5), rand_range(-20,-20))),
				 Transform(Basis(), Vector3(rand_range(20,20), rand_range(5,5), rand_range(40,40)))]

func init_cam_pos() -> Transform:
	return $Camera.global_transform


func get_spawn_position(hint: String) -> Transform:
	var spawnLocation = locations.pop_front()
	locations.push_back(spawnLocation)
	return spawnLocation
