libdir = $(plugindir)

lib_LTLIBRARIES = full_search.la

full_search_la_SOURCES = full_search.c
full_search_la_LDFLAGS = -module -no-undefined -avoid-version

INCLUDES = \
	-I$(top_srcdir) \
	-I$(top_srcdir)/libsylph \
	-I$(top_srcdir)/src \
	$(GTK_CFLAGS)

full_search_la_LIBADD = \
	$(GTK_LIBS) \
	$(top_builddir)/src/libsylpheed-plugin-0.la \
	$(top_builddir)/libsylph/libsylph-0.la

install:
install-plugin: install-am
