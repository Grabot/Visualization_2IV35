kernel void HairCalculations(constant const int *startindex, constant const int *endindex, 
				global float4 *force, global float *deltaT,
				global float4 *pos, global float4 *vel, global float4 *pred_pos)
{
	unsigned int xid = get_global_id(0);

	float particledistance = 5;
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

	}	

	for(int i = startindex[xid]+1; i < endindex[xid]; i++)
	{
		pos[i] = pred_pos[i];
	}
}
