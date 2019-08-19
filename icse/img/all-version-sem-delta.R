library(plotly)

ECMAScript_Version <- c("2015-16", "2016-17", "2017-18", "2019-20")
auto <- c(256, 123, 121, 155)
manual <- c(217, 139, 108, 280)
data <- data.frame(ECMAScript_Version, auto, manual)

p <- plot_ly(
  data,
  y = ~ECMAScript_Version,
  x = ~auto,
  type = 'bar',
  name = 'auto',
  marker = list(color = '#0070DE'),
  orientation = 'h'
) %>% add_trace(
  x = ~manual,
  name = 'manual',
  marker = list(color = '#FE1A13')
) %>% layout(
  xaxis = list(title = '# of Steps'),
  yaxis = list(title = '', showticklabels = FALSE),
  barmode = 'stack'
)
p