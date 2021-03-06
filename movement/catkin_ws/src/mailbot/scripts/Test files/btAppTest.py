#!/usr/bin/env python
import sys
import bluetooth
import time
from ast import literal_eval as make_tuple
import rospy
from std_msgs.msg import String

# def talker(pub_str):
#     pub = rospy.Publisher('deliveryLocations', String)
#     rospy.init_node('deliverLocationPub', anonymous=True)
#     rospy.loginfo(pub_str)
#     pub.publish(pub_str)

def getLockerToOpen(btSocket):
    print "getLockerToOpen()"
    btSocket.send("1409")
    while 1:
        appMsg = btSocket.recv(1024)
        if not appMsg:
            continue
        elif str(appMsg) == "RLO":
            print str(appMsg)
            # should write RLO to terminal
            btSocket.send("num")
            continue
        else:
            print appMsg
            # should print a number (the locker to be opened)
            break
        time.sleep(0.01)

def getLocationList(btSocket):
    print "getLocationList()"
    btSocket.send("3020")
    while 1:
        appMsg = btSocket.recv(1024)
        if not appMsg:
            continue
        elif str(appMsg) == "SLL":
            print str(appMsg)
            # should write SLL to terminal
            btSocket.send("list")
            continue
        else:
            print appMsg
            # should print a space delimited list of locations
            return appMsg
        time.sleep(0.01)

def notifyArrival(btSocket):
    print "notifyArrival()"
    btSocket.send("8062")
    while 1:
        appMsg = btSocket.recv(1024)
        if not appMsg:
            continue
        elif str(appMsg) == "6208":
            print str(appMsg)
            # should print 1409
            btSocket.send("adm")
            continue
        elif str(appMsg) == "ref":
            print str(appMsg)
            # should print ref
            btSocket.send("l,508")
            # should really be sending l,destinationNumber
            break
        time.sleep(0.01)

def getDeliveryComplete(btSocket):
    print "getDeliveryComplete()"
    btSocket.send("5060")
    while 1:
        appMsg = btSocket.recv(1024)
        if not appMsg:
            continue
        elif str(appMsg) == "GTN":
            print str(appMsg)
            # should write SLL to terminal
            btSocket.send("done")
            continue
        else:
            print str(appMsg)
            # should print a space delimited list of locations
            # return appMsg
            break
        time.sleep(0.01)

uuid = "00001105-0000-1000-8000-00805F9B34FB"
# tabAddress = "74:04:2b:e8:19:86"
tabAddress = "74:04:2B:D5:19:86"

service_matches = []

while not service_matches:
    service_matches = bluetooth.find_service( name = 'MYAPP', address = tabAddress )

    give me the puussss$$$$yyyyyy b0ss

print service_matches

# Is there a timeout that would let the script get to this if statement though
if len(service_matches) == 0:
    print "couldn't find the FooBar service"
    sys.exit(0)

first_match = service_matches[0]
port = first_match["port"]
name = first_match["name"]
host = first_match["host"]

print "connecting to \"%s\" on %s" % (name, host)

sock=bluetooth.BluetoothSocket( bluetooth.RFCOMM )
sock.connect((host, port))

# make_tuple("(1,2,3,4,5)") # NOTE: There must be no spaces in between the numbers

start = False
waitingToArrive = False
test = 0
# sock.send("hello!!")
while 1:

    if waitingToArrive:
        # gets here when it is waiting to arrive at a location
        print "Waiting to arrive"
        # arrivalCount = 0
        time.sleep(3)
        if test < 2:
            print "not test"
            notifyArrival(sock)
            # arrivalCount = arrivalCount + 1
            # print "Arrival count = " + str(arrivalCount)
            test = True
            # should wait for some message from ROS
            # if at location do notifyArrival(sock)

    try:
        print "getting data"
        data = sock.recv(1024)
        # sock.recv blocks lmao
        print("received [%s]" % data)
            # try:
            #     talker(str(data))
            # except rospy.ROSInterruptException:
            #     pass
            #     # break

        if not start:
            # Should only come here if the app and computer have not connected
            print "not start"
            print str(data)
            if str(data) == "hey":
                start = True
                print "started"
                continue

        # should always come here once the app and computer have connected
        if not waitingToArrive:
            if str(data) == "0507":
                getLockerToOpen(sock)
            elif str(data) == "0203":
                locationList = getLocationList(sock)
                print str(locationList)
                # waitingToArrive means the MailBot is now moving
                # it is waiting to arrive at a mail item destination
                waitingToArrive = True
                print "waitingToArrive = true"
            continue
        else:
            if str(data) == "0507":
                getLockerToOpen(sock)
            if str(data) == "0605":
                getDeliveryComplete(sock)
            continue

    except IOError:
        print "Error"
        pass
    time.sleep(0.01)
sock.close()
