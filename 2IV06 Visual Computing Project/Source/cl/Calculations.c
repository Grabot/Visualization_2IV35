constant int width = 32;
constant int height = 32;
constant int depth = 32;

constant float friction = 0.5f;
constant float repulsion = -0.2f;
		
unsigned int getKey(float spacing, float4 position) {
	int x = (int) floor(position.x / spacing);
	int y = (int) floor(position.y / spacing);
	int z = (int) floor(position.z / spacing);

	return x + width * (y + height * z);;
}

float4 getReferencePosition(float spacing, float4 position) {
	return floor(position / spacing) * spacing;
}

void addValues(float spacing, float **grid_weight, float4 **grid_vel, float4 position, float weight, float4 velocity) {
	float4 nodePosition = getReferencePosition(spacing, position);
	
	float xd = (position.x - nodePosition.x) / (spacing);
	float yd = (position.y - nodePosition.y) / (spacing);
	float zd = (position.z - nodePosition.z) / (spacing);
	
	float w1 = weight * xd * yd * zd;
	float w2 = weight * (1 - xd) * yd * zd;
	float w3 = weight * xd * (1 - yd) * zd;
	float w4 = weight * (1 - xd) * (1 - yd) * zd;
	float w5 = weight * xd * yd * (1 - zd);
	float w6 = weight * (1 - xd) * yd * (1 - zd);
	float w7 = weight * xd * (1 - yd) * (1 - zd);
	float w8 = weight * (1 - xd) * (1 - yd) * (1 - zd);
	
	float4 x1 = nodePosition;
	float4 x2 = nodePosition + (float4)(spacing, 0, 0, 0);
	float4 x3 = nodePosition + (float4)(0, spacing, 0, 0);
	float4 x4 = nodePosition + (float4)(spacing, spacing, 0, 0);
	float4 x5 = nodePosition + (float4)(0, 0, spacing, 0);
	float4 x6 = nodePosition + (float4)(spacing, 0, spacing, 0);
	float4 x7 = nodePosition + (float4)(0, spacing, spacing, 0);
	float4 x8 = nodePosition + (float4)(spacing, spacing, spacing, 0);
	
	(*grid_weight)[getKey(spacing, x1)] += w1;
	(*grid_weight)[getKey(spacing, x2)] += w2;
	(*grid_weight)[getKey(spacing, x3)] += w3;
	(*grid_weight)[getKey(spacing, x4)] += w4;
	(*grid_weight)[getKey(spacing, x5)] += w5;
	(*grid_weight)[getKey(spacing, x6)] += w6;
	(*grid_weight)[getKey(spacing, x7)] += w7;
	(*grid_weight)[getKey(spacing, x8)] += w8;
	
	(*grid_vel)[getKey(spacing, x1)] += velocity * w1;
	(*grid_vel)[getKey(spacing, x2)] += velocity * w2;
	(*grid_vel)[getKey(spacing, x3)] += velocity * w3;
	(*grid_vel)[getKey(spacing, x4)] += velocity * w4;
	(*grid_vel)[getKey(spacing, x5)] += velocity * w5;
	(*grid_vel)[getKey(spacing, x6)] += velocity * w6;
	(*grid_vel)[getKey(spacing, x7)] += velocity * w7;
	(*grid_vel)[getKey(spacing, x8)] += velocity * w8;
}

kernel void HairCalculations(constant const int *startindex, constant const int *endindex, 
							 global float4 *force, global float *deltaT,
							 global float4 *pos, global float4 *vel, global float4 *pred_pos,
							 global const float *spacing, global float *grid_weight, global float4 *grid_vel)
{
	unsigned int xid = get_global_id(0);

	float particledistance = 1;
	float correctionScale = 0.9;

	pred_pos[startindex[xid]] = pos[startindex[xid]];
	for(int i = startindex[xid]+1; i < endindex[xid]; i++)
	{
		pred_pos[i] = pos[i] + deltaT[0] * vel[i] + deltaT[0] * deltaT[0] * force[0];

		float4 position = pred_pos[i];
		float4 delta = normalize(pred_pos[i] - pred_pos[i-1]);
		pred_pos[i] = pred_pos[i-1] + delta * particledistance;
		float4 ftl_correction_vector = position - pred_pos[i];

		vel[i-1] = ((pred_pos[i-1] - pos[i-1]) / deltaT[0])+ (ftl_correction_vector /deltaT[0]) *correctionScale;;
		vel[i] =(pred_pos[i] - pos[i]) / deltaT[0];

		/*addValues(spacing[0], &grid_weight, &grid_vel, pred_pos[i], 1, vel[i]);*/

	}	

	for(int i = startindex[xid] + 1; i < endindex[xid]; i++)
	{
		pos[i] = pred_pos[i];
	}
}

kernel void GridCalculations(global const float *spacing, global float *grid_weight, global float4 *grid_vel, global float4 *grid_grad)
{
	unsigned int xid = get_global_id(0);
	
	if (grid_weight[xid] > 0) {
		
		float x1 = grid_weight[xid + 1];
		float x2 = grid_weight[xid - 1];
		float y1 = grid_weight[xid + width];
		float y2 = grid_weight[xid - width];
		float z1 = grid_weight[xid + width * height];
		float z2 = grid_weight[xid - width * height];
		
		grid_grad[xid] = ((float4)((x1 - x2), (y1 - y2), (z1 - z2), 0)) / (2 * spacing[0]);
		grid_vel[xid] /= grid_weight[xid];
	}
}

kernel void ParticleCalculations(global const float *spacing, global float *deltaT,
								 global float4 *pos, global float4 *vel, 
								 global float *grid_weight, global float4 *grid_vel, global float4 *grid_grad)
{
	unsigned int xid = get_global_id(0);
	
	float x = pos[xid].x;
	float y = pos[xid].y;
	float z = pos[xid].z;

	float4 n0 = getReferencePosition(spacing[0], pos[xid]);

	float x0 = n0.x;
	float y0 = n0.y;
	float z0 = n0.z;

	float xd = (x - x0) / spacing[0];
	float yd = (y - y0) / spacing[0];
	float zd = (z - z0) / spacing[0];

	int n000 = getKey(spacing[0], (float4)(x0, y0, z0, 0));
	int n010 = getKey(spacing[0], (float4)(x0, y0 + spacing[0], z0, 0));
	int n001 = getKey(spacing[0], (float4)(x0, y0, z0 + spacing[0], 0));
	int n011 = getKey(spacing[0], (float4)(x0, y0 + spacing[0], z0 + spacing[0], 0));
	int n100 = getKey(spacing[0], (float4)(x0 + spacing[0], y0, z0, 0));
	int n110 = getKey(spacing[0], (float4)(x0 + spacing[0], y0 + spacing[0], z0, 0));
	int n101 = getKey(spacing[0], (float4)(x0 + spacing[0], y0, z0 + spacing[0], 0));
	int n111 = getKey(spacing[0], (float4)(x0 + spacing[0], y0 + spacing[0], z0 + spacing[0], 0));

	float4 v00 = grid_vel[n000] * (1 - xd) + grid_vel[n100] * xd;
	float4 v10 = grid_vel[n010] * (1 - xd) + grid_vel[n110] * xd;
	float4 v01 = grid_vel[n001] * (1 - xd) + grid_vel[n101] * xd;
	float4 v11 = grid_vel[n011] * (1 - xd) + grid_vel[n111] * xd;

	float4 v0 = v00 * (1 - yd) + v10 * yd;
	float4 v1 = v01 * (1 - yd) + v11 * yd;

	float4 v = v0 * (1 - zd) + v1 * zd;
	
	/*vel[xid] = vel[xid] * (1 - friction) + v * friction;*/
	/*vel[xid] += (grid_grad[n000] * repulsion) / deltaT[0];*/
}