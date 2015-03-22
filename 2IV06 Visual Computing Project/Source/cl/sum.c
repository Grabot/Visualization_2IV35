kernel void ParticleCalculations(global float4 *pos, global float4 *vel, global float4 *grid_weight, global float4 *grid_velocity, global float grid_weight)
{
	unsigned int xid = get_global_id(0);

	float spacing = 5.0f;

	float4 refPosition = floor(pos[dix] / spacing) * spacing;
	pos[xid] = refPosition;


	// Difference between coordinates
		float xd = (pos[xid].x - nodePosition.x) / (spacing);
		float yd = (pos[xid].y - nodePosition.y) / (spacing);
		float zd = (pos[xid].z - nodePosition.z) / (spacing);

		// Get all eight node values
		float w1 = weight * xd * yd * zd;
		float w2 = weight * (1 - xd) * yd * zd;
		float w3 = weight * xd * (1 - yd) * zd;
		float w4 = weight * (1 - xd) * (1 - yd) * zd;
		float w5 = weight * xd * yd * (1 - zd);
		float w6 = weight * (1 - xd) * yd * (1 - zd);
		float w7 = weight * xd * (1 - yd) * (1 - zd);
		float w8 = weight * (1 - xd) * (1 - yd) * (1 - zd);

}
