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
		tooltips: 
		{
		    enabled: true,
		    type: "placeholder",
		    string: "{label}: {percentage}%",
		    styles: 
		    {
				fadeInSpeed: 500,
				backgroundColor: "#111111",
				backgroundOpacity: 0.8,
				color: "#ffffff",
				borderRadius: 4,
				font: "verdana",
				fontSize: 20,
				padding: 20
		    }
  		},
  		effects:
  		{
			load: 
			{
				speed: 1000
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
	d.style.left =  "700px";
	d.style.width = "500px";
	d.style.height = "500px";

	//end piechart
	return pie;
}