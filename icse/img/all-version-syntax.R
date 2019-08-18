library(plotly)

ECMAScript_Version <- c(2016, 2017, 2018, 2019, 2020)
existing <- c(0, 270, 290, 300, 325)
auto <- c(280, 30, 20, 30, 10)
manual <- c(3, 0, 0, 0, 0)
data <- data.frame(ECMAScript_Version, existing, auto, manual)

p <- plot_ly(
  data,
  x = ~ECMAScript_Version,
  y = ~existing,
  type = 'bar',
  name = 'existing',
  marker = list(color = 'black')
) %>% add_trace(
  y = ~auto,
  name = 'auto',
  marker = list(color = '#0070DE')
) %>% add_trace(
  y = ~manual,
  name = 'manual',
  marker = list(color = '#FE1A13')
) %>% layout(
  yaxis = list(title = '# of Productions'),
  xaxis = list(title = 'ECMAScript Versions', dtick = 1),
  barmode = 'stack'
)
p