library(plotly)

ECMAScript_Version <- c("2015-16", "2016-17", "2017-18", "2019-20")
auto <- c(160, 100, 240, 180)
manual <- c(10, 3, 7, 8)
data <- data.frame(ECMAScript_Version, auto, manual)

p <- plot_ly(
  data,
  x = ~ECMAScript_Version,
  y = ~auto,
  type = 'bar',
  name = 'auto',
  marker = list(color = '#0070DE')
) %>% add_trace(
  y = ~manual,
  name = 'manual',
  marker = list(color = '#FE1A13')
) %>% layout(
  yaxis = list(title = '# of Steps'),
  xaxis = list(title = 'ECMAScript Versions', dtick = 1),
  barmode = 'stack'
)
p