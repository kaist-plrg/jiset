FILE=main

TEXSRC=$(wildcard *.tex *.bib img/*.pdf)

$(FILE).pdf: $(TEXSRC)
	pdflatex $(FILE).tex

.PHONY: clean

clean:
	rm -rf *~ *.aux *.log *.out *.bbl *.blg *.bak *.vtc $(FILE).pdf
