import shutil
import os, fnmatch
import sys
import http.server
import socketserver

def findReplace(directory, find, replace, filePattern):
    for path, dirs, files in os.walk(os.path.abspath(directory)):
        for filename in fnmatch.filter(files, filePattern):
            filepath = os.path.join(path, filename)
            with open(filepath) as f:
                s = f.read()
            s = s.replace(find, replace)
            with open(filepath, "w") as f:
                f.write(s)

if not os.path.isdir('dist_changed'):
    shutil.copytree('dist', 'dist_changed')
    findReplace("dist_changed", "{API_URL}", sys.argv[1], "*")
    findReplace("dist_changed", "{REST_URL}", sys.argv[2], "*")
    findReplace("dist_changed", "/StoryGame", "", "*")

PORT = 8000
DIRECTORY = "dist_changed"

class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

with socketserver.TCPServer(("", PORT), Handler) as httpd:
    print("serving at port", PORT)
    httpd.serve_forever()