library(plotly)

ECMAScript_Version <- c(2016, 2017, 2018, 2019, 2020)
existing <- c(0, 4000, 4110, 5140, 5400)
auto <- c(5000, 1800, 2070, 2090, 2300)
manual <- c(200, 50, 100, 30, 20)
data <- data.frame(ECMAScript_Version, existing, auto, manual)

p <- plot_ly(
  data,
  x = ~ECMAScript_Version,
  y = ~existing,
  type = 'bar',
  name = 'existing',
  marker = list(color = 'black'),
  dtick = 1
) %>% add_trace(
  y = ~auto,
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