import datetime
	import time
	

	from bluepy.btle import Scanner, DefaultDelegate, BTLEException, Peripheral, UUID
	

	

	student_number_service = UUID("4CC28E11-4465-4136-B84C-AB34109B3D87")
	student_number_characteristic = UUID("8FB63C83-2AAC-44DC-8FB6-6BE9257CBDD1")
	

	

	class ScanDelegate(DefaultDelegate):
	    def __init__(self):
	        DefaultDelegate.__init__(self)
	

	    def handleDiscovery(self, dev, isNewDev, isNewData):
	        pass
	        #print("Discovery: {0}\t{1}".format(dev.addr, dev.addrType))
	

	

	class AttendanceTracker(object):
	    def __init__(self, lecture):
	        self.lecture = lecture
	        self.data = dict()
	        self.scanner = Scanner().withDelegate(ScanDelegate())
	

	    def start_scanning(self, duration):
	        students_scanned = 0
	        devices = self.scanner.scan(duration)
	        #print("{0} devices".format(len(devices)))
	        for dev in devices:
	            for adtype, desc, value in dev.getScanData():
	                if adtype == 0x07:
	                    if dev.connectable and dev.addrType == "random":
	                        student_number = self.read_student_number(dev.addr)
	                        print("Student Num: {0}".format(student_number))
	                        self.register_student(student_number)
	                        students_scanned += 1
	                    #print("Connectable: {0}".format(dev.connectable))
	        return students_scanned
	

	    def read_student_number(self, mac_address):
	        start = time.time()
	        device = Peripheral(mac_address, addrType="random")
	        service = device.getServiceByUUID(student_number_service)
	        chars = device.getCharacteristics(uuid=student_number_characteristic)
	        print(service)
	        print(chars)
	        for char in chars:
	            print(char, char.getHandle(), char.propertiesToString(), char.supportsRead())
	

	        student_number = chars[-1].read()
	        end = time.time()
	        print("{0} seconds".format(end - start))
	        return student_number.decode('utf-8')
	

	    def register_student(self, student_number):
	        if self.data.get(student_number):
	            self.data[student_number] += 1
	        else:
	            self.data[student_number] = 1
	

	    def print_data(self):
	        print(self.data)
	

	    def record(self):
	        print("Recording attendance for: {0}".format(self.lecture))
	

	        print("Scanning attendance for 1st time...(1/2)")
	        scans = self.start_scanning(duration=45.0)
	        print("{0} student numbers scanned during first scan".format(scans))
	

	        print("Sleeping")
	        time.sleep(5)
	

	        print("Scanning attendance for 2nd time...(2/2)")
	        scans = self.start_scanning(duration=45.0)
	        print("{0} student numbers scanned during second scan".format(scans))
	

	        self.print_data()
	        return self.data
	

	

	if __name__ == '__main__':
	    tracker = AttendanceTracker(lecture="Security & Privacy")
	

	    start = time.time()
	    tracker.record()
	    end = time.time()
	    print("{0} seconds".format(end - start))
