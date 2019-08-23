library(plotly)

ECMAScript_Version <- c(2016, 2017, 2018, 2019, 2020)
auto <- c(938, 1055, 1109, 1127, 1145)
manual <- c(475, 436, 446, 474, 456)
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
  yaxis = list(title = '# of Steps', dtick = 200), # , autorange = "reversed"),
  xaxis = list(title = 'ECMAScript Versions', side = "right", dtick = 1),
  legend = list(orientation = 'h', x = 0.1, y = 1.05, bgcolor='rgba(0,0,0,0)'),
  barmode = 'stack'
)
p