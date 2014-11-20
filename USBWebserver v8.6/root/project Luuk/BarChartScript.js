﻿var height = 200;
var width = 400;
var isDrawn = false;
var y;
var x;

function draw(selectedCity) {

    if (!isDrawn) {
        var spacing = 5;
        var margin = { top: 20, right: 30, bottom: 60, left: 40 };
        var barWidth = 40;

        var chart = d3.select(".chart");
        if (!chart.empty()) {

            if (chart.attr("width") != null) {
                width = chart.attr("width");
            }

            if (chart.attr("height") != null) {
                height = chart.attr("height");
            }

            height = height - margin.top - margin.bottom;
            width = width - margin.left - margin.right;
            console.log(selectedCity.size());
            barWidth = ((width - (selectedCity.size() * spacing)) / selectedCity.size());

            x = d3.scale.ordinal().rangeRoundBands([0, width], 1).domain(selectedCity.keys());
            y = d3.scale.linear().range([height, 0]).domain([0, d3.max(selectedCity.values()) / 100]);

            var xAxis = d3.svg.axis().scale(x).orient("bottom");
            var yAxis = d3.svg.axis().scale(y).orient("left").ticks(10, "%");

            chart = chart.attr("width", width + margin.left + margin.right)
                .attr("height", height + margin.top + margin.bottom)
                .append("g")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

            chart.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + height + ")")
                .call(xAxis)

            chart.append("g")
                .attr("class", "y axis")
                .call(yAxis)
                .append("text")
                .attr("transform", "rotate(-90)")
                .attr("y", 6)
                .attr("dy", ".71em")
                .style("text-anchor", "end")
                .text("Percentage");

            chart.selectAll(".bar")
                .data(selectedCity.entries())
                .enter().append("rect")
                .attr("class", "bar")
                .attr("x", function (d, index) { return x(d.key) - (0.5 * barWidth); })
                .attr("y", function (d) { return y(0); })
                .attr("width", barWidth)
                .attr("height", function (d) { return height - y(0); })
                .on("mousedown", barClick);

            isDrawn = true;
            animatedBarChartUpdate(selectedCity);
        }
    } else {
        animatedBarChartUpdate(selectedCity);
    }
};

function barClick(d) {
    alert("De waarde is: " + d.value);
};


function animatedBarChartUpdate(selectedCity) {

    d3.selectAll(".bar").transition()
            .duration(1000)
            .attr("y", function (d) { return height - 0; })
            .attr("height", function (d) { return 0; });

    d3.selectAll(".bar").transition()
            .duration(1000)
            .delay(1000)
            .attr("y", function (d) { return y(selectedCity.get(d.key) / 100); })
            .attr("height", function (d, i, h) { return height - y(selectedCity.get(d.key) / 100); });
};