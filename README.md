#Naughts and Crosses

By Nat Egan-Pimblett

An Om/core.async implementation of naughts and crosses. 

You can try it out [here](neganp.github.io/static/nac).

To get it set up for yourself, you'll need Leiningen installed (and a
SASS processor of your choice to compile `style.scss`). Clone the
repository, do `lein cljsbuild once`, and start a web-server at the
top-level directory (I use `python -m SimpleHTTPServer`).

Point your browser the server you just started (if you're using the
Python suggestion, that's `localhost:8000` by default), and you should
see a page like the demo.

The demo is compiled with advanced optimizations, so it may be a little
different than what's shown here.

Happy Clourescript hacking!
