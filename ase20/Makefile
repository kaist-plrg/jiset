TEXSRC=	main.tex intro.tex overview.tex \
				parser.tex compiler.tex impl.tex \
				eval.tex related.tex conclusion.tex \
				macros.tex ref.bib

main.pdf: $(TEXSRC)
	pdflatex main.tex  && \
	bibtex main && \
	pdflatex main.tex && \
	pdflatex main.tex

.PHONY: clean

clean:
	rm -rf *~ *.aux *.log *.out *.bbl *.blg *.bak *.vtc main.pdf
