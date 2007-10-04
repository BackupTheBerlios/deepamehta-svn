#!/bin/sh

if [ "$ANT_HOME" != "" ];then
	if [ ! -x "$ANT_HOME/bin/ant" ];then
		export ANT_HOME=
	else
		vers=$("$ANT_HOME/bin/ant" -version 2>/dev/null \
		| sed -e 's,.* \([0-9]*\.[0-9]*\.[0-9]*\) .*,\1,g'|tr -d -c '0-9.')
		majorVer=$(echo $vers|cut -d. -f1)
		minorVer=$(echo $vers|cut -d. -f2)
		stepVer=$(echo $vers|cut -d. -f3)
		if [ "$stepVer" != "" ] ; then
			if	[ $majorVer -lt 1 ] \
			||	(	[ $majorVer -eq 1 ] \
				&&	[ $minorVer -lt 7 ] )
			then
				echo "Your ant installation (version $vers) is not suiteable. Using my own!" >&2
				export ANT_HOME=
			fi
		else
			echo "Your ant installation is not suiteable. Using my own!" >&2
			export ANT_HOME=
		fi
	fi
fi

current="$(pwd)"
cd "$(dirname $0)"

#if [ "$ANT_HOME" = "" ];then
export ANT_HOME="$(pwd)/ant"
#fi

cd  "$current"

${ANT_HOME}/bin/ant $*
