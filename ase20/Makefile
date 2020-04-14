TEXSRC=	main.tex abstract.tex intro.tex \
				overview.tex parser.tex compiler.tex \
				eval.tex related.tex conclusion.tex \
				macros.tex ref.bib

all: main.pdf report/report.pdf

main.pdf: $(TEXSRC)
	pdflatex main.tex  && \
	bibtex main && \
	pdflatex main.tex && \
	pdflatex main.tex

report/report.pdf:
	cd report; $(MAKE)

.PHONY: clean

clean:
	rm -rf *~ *.aux *.log *.out *.bbl *.blg *.bak *.vtc main.pdf
