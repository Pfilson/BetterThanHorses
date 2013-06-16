import os, re

clientClassesFile = 'client-classes'

clientPath = os.path.join('client','net','minecraft','src')
serverPath = os.path.join('server','net','minecraft','src')

clientProxyClass = 'BTHProxyClient'
serverProxyClass = 'BTHProxy'

# Currently, searches for the BTHProxyClient class being constructed when replacing references to it.
proxySearchPattern = '(new\\s+)' + clientProxyClass
proxySearchReplace = '\\1' + serverProxyClass

# Make server directory if it doesn't exist
try:
  os.makedirs(serverPath)
except OSError:
  pass



f = open(clientClassesFile, 'r')
clientClasses = [line.strip() for line in f.readlines()]
f.close()

sourceClasses = os.listdir(clientPath)

for sc in sourceClasses:
  shouldCopy = True
  for cc in clientClasses:
    if sc == cc:
      shouldCopy = False
      break
  if shouldCopy:
    inFile  = open(os.path.join(clientPath, sc), 'r')
    outFile = open(os.path.join(serverPath, sc), 'w')
    for line in inFile.readlines():
      # Replace references to client proxy class with server proxy class
      modifiedLine = re.sub(proxySearchPattern, proxySearchReplace, line)
      outFile.write(modifiedLine)
    inFile.close()
    outFile.close()
