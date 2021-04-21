FILE=report

TEXSRC=$(wildcard *.tex)

$(FILE).pdf: $(TEXSRC)
	pdflatex $(FILE).tex && \
	pdflatex $(FILE).tex

.PHONY: clean

clean:
	rm -rf *~ *.aux *.log *.out *.bbl *.blg *.bak *.vtc $(FILE).pdf
