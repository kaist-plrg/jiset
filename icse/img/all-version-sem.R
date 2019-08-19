library(plotly)

ECMAScript_Version <- c(2016, 2017, 2018, 2019, 2020)
auto <- c(938, 1055, 1109, 1127, 1145)
manual <- c(475, 436, 446, 474, 456)
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
  xaxis = list(title = '# of Steps', autorange = "reversed"),
  yaxis = list(title = 'ECMAScript Versions', side = "right", dtick = 1),
  barmode = 'stack'
)
p