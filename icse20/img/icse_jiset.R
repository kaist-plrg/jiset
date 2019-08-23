library(ggplot2)
library(scales)

# copy below lines
if (FALSE) {
  dir = "~/project/ase/" # fill the project directory path
  source(file = paste(dir, "icse/img/icse_jiset.R", sep = "", collapse = NULL))
  getGraph(dir)
}

getGraph <- function(dir) {
  d <- read.csv(paste(dir, "icse/img/data.csv", sep = "", collapse = NULL), header = TRUE)
  p <- ggplot(data=d, aes(x = pos, fill=kind)) + 
    geom_rect(aes(xmin = pos - width/2, xmax = pos + width/2, ymin = end, ymax = start)) +
    labs(title="", x="Version of ECMAScript", y="# of Steps", fill="") +
    theme(
      panel.background = element_blank(),
      element_line(colour = 'black', size=0.5, linetype='solid'),
      axis.line = element_line(colour = "black")
    ) + scale_fill_manual(values=c("#0070DE", "#9FC5E2", "#FE1A13"))
  return(p)
}