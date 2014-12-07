
	function fillmappCars()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["white", "red"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"white");
		grd.addColorStop(1,"red");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
			//console.log("test: " + population[x].AUTO_TOT );
			if( population[x].AUTO_TOT != -99999998.0 )
	   		{
	   			cityData.set(population[x].GM_CODE, +population[x].AUTO_TOT);
	   		}
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());	
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

	function fillmappInhabitants()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["lightblue", "red"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"lightblue");
		grd.addColorStop(1,"red");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
	   		cityData.set(population[x].GM_CODE, +population[x].AANT_INW);
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

	function fillmappRatio()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["#ff2030", "#20f0ff"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"#20f0ff");
		grd.addColorStop(1,"#ff2030");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
   			manPerc = (population[x].AANT_MAN / population[x].AANT_INW);
	   		femalePerc = (population[x].AANT_VROUW / population[x].AANT_INW);

	   		numberOfFemales = ((manPerc -femalePerc)*population[x].AANT_INW);
	   		cityData.set(population[x].GM_CODE, +numberOfFemales);
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());	
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

	function fillmappEuropeans()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["green", "red"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"green");
		grd.addColorStop(1,"red");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
	   		cityData.set(population[x].GM_CODE, +population[x].P_WEST_AL);
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());		
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

	function fillmappNotEuropeans()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["green", "red"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"green");
		grd.addColorStop(1,"red");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
	   		cityData.set(population[x].GM_CODE, +population[x].P_N_W_AL);
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());		
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

	function fillmappMoroccans()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["green", "red"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"green");
		grd.addColorStop(1,"red");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
	   		cityData.set(population[x].GM_CODE, +population[x].P_MAROKKO);
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());		
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

	function fillmappTurkish()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["green", "red"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"green");
		grd.addColorStop(1,"red");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
	   		cityData.set(population[x].GM_CODE, +population[x].P_TURKIJE);
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());	
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

	function fillmappMoroccans()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["green", "red"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"green");
		grd.addColorStop(1,"red");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
	   		cityData.set(population[x].GM_CODE, +population[x].P_MAROKKO);
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());	
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

	function fillmappAntilles()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["green", "red"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"green");
		grd.addColorStop(1,"red");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
	   		cityData.set(population[x].GM_CODE, +population[x].P_ANT_ARU);
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());	
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

	function fillmappSurinames()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["green", "red"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"green");
		grd.addColorStop(1,"red");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
	   		cityData.set(population[x].GM_CODE, +population[x].P_SURINAM);
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());		
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

	function fillmappnotMentioned()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["green", "red"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"green");
		grd.addColorStop(1,"red");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
	   		cityData.set(population[x].GM_CODE, +population[x].P_OVER_NW);
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());		
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

	function fillmappnotMarried()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["blue", "yellow"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"blue");
		grd.addColorStop(1,"yellow");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
	   		cityData.set(population[x].GM_CODE, +population[x].P_ONGEHUWD);
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());	
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

	function fillmappMarried()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["blue", "yellow"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"blue");
		grd.addColorStop(1,"yellow");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
	   		cityData.set(population[x].GM_CODE, +population[x].P_GEHUWD);
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());	
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

	function fillmappDivorced()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["blue", "yellow"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"blue");
		grd.addColorStop(1,"yellow");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
	   		cityData.set(population[x].GM_CODE, +population[x].P_GESCHEID);
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());	
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

	function fillmappWater()
	{
		linearColorScale = d3.scale.linear()
		.domain([0.0, 100.0])
		.range(["#B3432B", "blue"]);

		// Create gradient
		grd = ctx.createLinearGradient(0,0,500,30);
		grd.addColorStop(0,"blue");
		grd.addColorStop(1,"#B3432B");

		// Fill with gradient
		ctx.fillStyle = grd;
		ctx.fillRect(0,0,500,30);

		for( x = 0; x < (population.length-1); x++ )
		{
	   		cityData.set(population[x].GM_CODE, +(population[x].OPP_WATER/population[x].OPP_TOT));
		}

		var maxValue = d3.max(cityData.values());
		var minValue = d3.min(cityData.values());	
		
		linearColorScale.domain([minValue, maxValue]);

		drawHeatMap();
	}

function fillmappWidowed()
{
	linearColorScale = d3.scale.linear()
	.domain([0.0, 100.0])
	.range(["blue", "yellow"]);

	// Create gradient
	grd = ctx.createLinearGradient(0,0,500,30);
	grd.addColorStop(0,"blue");
	grd.addColorStop(1,"yellow");

	// Fill with gradient
	ctx.fillStyle = grd;
	ctx.fillRect(0,0,500,30);

	for( x = 0; x < (population.length-1); x++ )
	{
   		cityData.set(population[x].GM_CODE, +population[x].P_VERWEDUW);
	}

	var maxValue = d3.max(cityData.values());
	var minValue = d3.min(cityData.values());
	
	linearColorScale.domain([minValue, maxValue]);

	drawHeatMap();
}

function fillmappChildren()
{
	linearColorScale = d3.scale.linear()
	.domain([0.0, 100.0])
	.range(["white", "blue"]);

	// Create gradient
	grd = ctx.createLinearGradient(0,0,500,30);
	grd.addColorStop(0,"white");
	grd.addColorStop(1,"blue");

	// Fill with gradient
	ctx.fillStyle = grd;
	ctx.fillRect(0,0,500,30);

	for( x = 0; x < (population.length-1); x++ )
	{
   		cityData.set(population[x].GM_CODE, +population[x].P_HH_M_K);
	}

	var maxValue = d3.max(cityData.values());
	var minValue = d3.min(cityData.values());	
	
	linearColorScale.domain([minValue, maxValue]);

	drawHeatMap();
}

function fillmappDensity()
{
	linearColorScale = d3.scale.linear()
	.domain([0.0, 100.0])
	.range(["green", "red"]);

	// Create gradient
	grd = ctx.createLinearGradient(0,0,500,30);
	grd.addColorStop(0,"green");
	grd.addColorStop(1,"red");

	// Fill with gradient
	ctx.fillStyle = grd;
	ctx.fillRect(0,0,500,30);

	for( x = 0; x < (population.length-1); x++ )
	{
   		cityData.set(population[x].GM_CODE, +population[x].BEV_DICHTH);
	}

	var maxValue = d3.max(cityData.values());
	var minValue = d3.min(cityData.values());	
	
	linearColorScale.domain([minValue, maxValue]);

	drawHeatMap();
}

function fillmappMotor()
{
	linearColorScale = d3.scale.linear()
	.domain([0.0, 100.0])
	.range(["white", "blue"]);

	// Create gradient
	grd = ctx.createLinearGradient(0,0,500,30);
	grd.addColorStop(0,"white");
	grd.addColorStop(1,"blue");

	// Fill with gradient
	ctx.fillStyle = grd;
	ctx.fillRect(0,0,500,30);

	for( x = 0; x < (population.length-1); x++ )
	{
   		cityData.set(population[x].GM_CODE, +(population[x].MOTOR_2W/population[x].AUTO_TOT));
	}

	var maxValue = d3.max(cityData.values());
	var minValue = d3.min(cityData.values());	
	
	linearColorScale.domain([minValue, maxValue]);

	drawHeatMap();
}

function drawHeatMap()
{
	g.selectAll("path")
		.transition()
		.duration(1000)
		.style("fill", function(d) 
		{
			if( (d.gm_code == "GM0462") || (d.gm_code == "GM0463") || (d.gm_code == "GM0366") || (d.gm_code == "GM0412") )
			{
				return "white";
			}
			else
			{
				return linearColorScale(cityData.get(d.gm_code));
			}
		})
		/*
		.on("mouseout", function(){d3.select(this)
			.style("fill", function(d) 
			{
				if( (d.gm_code == "GM0462") || (d.gm_code == "GM0463") || (d.gm_code == "GM0366") || (d.gm_code == "GM0412") )
				{
					return "white";
				}
				else
				{
					return linearColorScale(cityData.get(d.gm_code));
				}
			}); 
		})
*/
}