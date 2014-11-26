function checkItem()
{
		for(count=0;count<4;count++)
		{
			if( MyForm.itempie[count].checked )
			{
				pieversion = count;
				if( count == 0 )
				{
					pie.updateProp( "data.content", pieDataMarital );
				}
				else if( count == 1 )
				{
					pie.updateProp( "data.content", pieDataRatio );
				}
				else if( count == 2 )
				{
					pie.updateProp( "data.content", pieDataInhabitants );
				}
				else if( count == 3 )
				{
					pie.updateProp( "data.content", pieDataWater );
				}
			}
		}
}


function checkItemVis()
{
	for(count=0;count<3;count++)
	{
		if( Visualization.item[count].checked )
		{
			if( count == 0 )
			{
				chartsettingsextended.style.visibility = "hidden";
				chartsettingssimple.style.visibility = "hidden";
				piesettings.style.visibility = "hidden";
				scartsettingscars.style.visibility = "visible";
				checkItemScatterExtend();
				pf.style.visibility = "hidden";
				h.style.visibility = "hidden";
				sc.style.visibility = "visible";
			}
			else if( count == 1 )
			{
				chartsettingsextended.style.visibility = "visible";
				chartsettingssimple.style.visibility = "hidden";
				checkItemExtend();
				piesettings.style.visibility = "hidden";
				scartsettingscars.style.visibility = "hidden";
				scartsettingsmotor.style.visibility = "hidden";
				pf.style.visibility = "hidden";
				h.style.visibility = "visible";
				sc.style.visibility = "hidden";
			}
			else if( count == 2 )
			{
				chartsettingsextended.style.visibility = "hidden";
				chartsettingssimple.style.visibility = "hidden";
				piesettings.style.visibility = "visible";
				checkItem();
				scartsettingscars.style.visibility = "hidden";
				scartsettingsmotor.style.visibility = "hidden";
				pf.style.visibility = "visible";
				h.style.visibility = "hidden";
				sc.style.visibility = "hidden";
			}
		}
	}
}

function checkItemScatterExtend()
{
	for(count=0;count<2;count++)
	{
		if( ScatterExtended.item[count].checked )
		{
			if( count == 0 )
			{
				getScatterDataCars();
				scartsettingscars.style.visibility = "visible";
				scartsettingsmotor.style.visibility = "hidden";
				scartsettingsdensity.style.visibility = "hidden";
				scattercar.data = scatterData;
				scattercar.draw();
			}
			else if( count == 1 )
			{
				getScatterDataMotorCycles();
				scartsettingscars.style.visibility = "hidden";
				scartsettingsmotor.style.visibility = "visible";
				scartsettingsdensity.style.visibility = "hidden";
				scattermotor.data = scatterData;
				scattermotor.draw();
			}
			else if( count == 2 )
			{
				getScatterDataDensity();
				scartsettingscars.style.visibility = "hidden";
				scartsettingsmotor.style.visibility = "hidden";
				scartsettingsdensity.style.visibility = "visible";
				scatterdensity.data = scatterData;
				scatterdensity.draw();
			}
		}
	}
}

function checkItemExtend()
{
	for(count=0;count<2;count++)
	{
		if( Extended.item[count].checked )
		{
			if( count == 0 )
			{
				chartsettingssimple.style.visibility = "visible";
				chartsettingsextended.style.visibility = "hidden";
				chartsimple.data = chartDataSimple;
				chartsimple.draw(1000);
				chartextendedvariable = 0;
			}
			else if( count == 1 )
			{
				chartsettingsextended.style.visibility = "visible";
				chartsettingssimple.style.visibility = "hidden";
				chartextended.data = chartDataExtended;
				chartextended.draw(1000);
				chartextendedvariable = 1;
			}
		}
	}
}

function checkItemMapData()
{
	for(count=0;count<16;count++)
	{
		if( MapDataSelection.item[count].checked )
		{
			if( count == 0 )
			{
				fillmappInhabitants();
			}
			else if( count == 1 )
			{
				fillmappWater();
			}
			else if( count == 2 )
			{
				fillmappChildren();
			}
			else if( count == 3 )
			{
				fillmappDensity();
			}
			else if( count == 4 )
			{
				fillmappRatio();
			}
			else if( count == 5 )
			{
				fillmappEuropeans();
			}
			else if( count == 6 )
			{
				fillmappNotEuropeans();
			}
			else if( count == 7 )
			{
				fillmappMoroccans();
			}
			else if( count == 8 )
			{
				fillmappAntilles();
			}
			else if( count == 9 )
			{
				fillmappSurinames();
			}
			else if( count == 10 )
			{
				fillmappTurkish();
			}
			else if( count == 11 )
			{
				fillmappnotMentioned();
			}
			else if( count == 12 )
			{
				fillmappnotMarried();
			}
			else if( count == 13 )
			{
				fillmappMarried();
			}
			else if( count == 14 )
			{
				fillmappDivorced();
			}
			else if( count == 15 )
			{
				fillmappWidowed();
			}
		}
	}
}
