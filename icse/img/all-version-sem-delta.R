library(plotly)

ECMAScript_Version <- c("2015-16", "2016-17", "2017-18", "2019-20")
auto <- c(256, 123, 121, 155)
manual <- c(217, 139, 108, 280)
data <- data.frame(ECMAScript_Version, auto, manual)

p <- plot_ly(
  data,
  x = ~ECMAScript_Version,
  y = ~auto,
  type = 'bar',
  name = 'auto',
  text = auto,
  textposition = 'auto',
  marker = list(color = '#0070DE')
  # orientation = 'h'
) %>% add_trace(
  y = ~manual,
  name = 'manual',
  text = manual,
  textposition = 'auto',
  marker = list(color = '#FE1A13')
) %>% layout(
  yaxis = list(title = '# of Steps', dtick = 50),
  xaxis = list(title = 'Δ ECMAScript Version'), #, showticklabels = FALSE),
  legend = list(orientation = 'h', x = 0.1, y = 1.05, bgcolor='rgba(0,0,0,0)'),
  barmode = 'stack'
)
p