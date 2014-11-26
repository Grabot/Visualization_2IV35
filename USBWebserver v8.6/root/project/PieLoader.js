var pieData;
var pieName;

function drawPie( pieData )
{

	//piechart
	var pieName = "Eemsmond";
	

	var pie = new d3pie("pie", 
	{
		header: 
		{
   			title: 
   			{
     			text: pieName,
      			fontSize: 20
		    }
		},
		data: 
		{
			content: pieData
		},
		size:
		{
			canvasWidth: 800
		},
  		labels: 
	  	{
	  		percentage: 
	  		{
	  			color: "#ffffff",
	  			fontSize: 20,
				decimalPlaces: 2
			},
			mainLabel: 
			{
				fontSize: 25
			},
			innerLabel:
			{
				fontSize: 13
			}
		},
  		effects:
  		{
			load: 
			{
				speed: 500
			},
			pullOutSegmentOnClick: 
			{
				effect: "elastic",
				speed: 200,
				size: 40
			}
		},
	});

	var d = document.getElementById('pie');
	d.style.position = "absolute";
	d.style.left =  "600px";
	d.style.width = "600px";
	d.style.height = "500px";

	//end piechart
	return pie;
}