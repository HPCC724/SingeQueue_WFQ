import math

import numpy as np
import csv
import sys
import os

#Problem: counted retransmited pkts

##################################
# Setup
#

#analyze for IAT in WFQ

print("NetBench python analysis tool v0.01")

# Usage print
def print_usage():
    print("Usage: python analyze.py /path/to/run/folder")

# Check length of arguments
if len(sys.argv) != 2:
    print("Number of arguments must be exactly two: analyze.py and /path/to/run/folder.")
    print_usage()
    exit()

# Check run folder path given as first argument
run_folder_path = sys.argv[1]
if not os.path.isdir(run_folder_path):
    print("The run folder path does not exist: " + run_folder_path)
    print_usage()
    exit()

# Create analysis folder
analysis_folder_path = run_folder_path + '/analysis'
if not os.path.exists(analysis_folder_path):
    os.makedirs(analysis_folder_path)



#class flow to manage flow info
class Flow:
    def __init__(self,flowID,source_id,target_id,sent_bytes,total_size_bytes,start_time,end_time,duration,completed):
        self.flowID = flowID
        self.source_id = source_id
        self.target_id = target_id
        self.sent_bytes = 0
        self.total_size_bytes = total_size_bytes
        self.start_time = start_time
        self.end_ent = end_time
        self.duration = duration
        self.completed = completed
        self.IATs = []
        self.ArrivalTimes = []
        self.pkt_bytes = []
        self.rates = []
        self.weight = 0
        self.current_pkt_time = 0
        self.current_seqnum = -1
        self.p00 = 0
        self.p01 = 0
        self.p10 = 0
        self.p11 = 0
        self.pp = 0.0
        self.ave_rate = 0
        self.current_state = -1          #设2个状态为0,1,normal,burst
        self.burst_duration = 0.0
        self.burst_bytes = 0.0

    def calcu_rate(self):
        self.ave_rate = self.sent_bytes/self.duration
    def calcu_pp(self,L):     #第一种方式：第一个和第二个各自除以第一个IAT的一半
        for i in range(0,len(self.IATs)):
            if(self.IATs[i] == 0):
                print(self.IATs)
                print(self.flowID)
                exit(0)
            self.rates.append(self.pkt_bytes[i]/self.IATs[i])
        for i in range(0,len(self.rates)):
            rate = self.rates[i]
            time = self.IATs[i]
            bytes = self.pkt_bytes[i]
            if(self.current_state == -1):
                if(rate > L*self.ave_rate):
                    self.current_state = 1
                    self.burst_duration += time
                    self.burst_bytes += bytes
                else:
                    self.current_state = 0
            elif self.current_state == 0:
                if(rate > L*self.ave_rate):
                    self.current_state = 1
                    self.p01 += 1
                    self.burst_duration += time
                    self.burst_bytes += bytes
                else:
                    self.current_state = 0
                    self.p00 += 1
            else:
                if(rate > L*self.ave_rate):
                    self.current_state = 1
                    self.p11 += 1
                    self.burst_duration += time
                    self.burst_bytes += bytes
                else:
                    self.current_state = 0
                    self.p10 += 1
        if(self.p10 != 0):
            self.pp = self.p11*1.0/self.p10
        else:
            self.pp = -1





##################################
# Analyze flow completion
#
def analyze_flow_completion():
    with open(run_folder_path + '/flow_completion.csv.log') as file:
        reader = csv.reader(file)

        # To enable preliminary read to determine size:
        # data = list(reader)
        # row_count = len(data)

        # Column lists
        flow_ids = []
        source_ids = []
        target_ids = []
        sent_bytes = []
        total_size_bytes = []
        start_time = []
        end_time = []
        duration = []
        completed = []

        print("Reading in flow completion log file...")

        # Read in column lists
        for row in reader:
            flow_ids.append(float(row[0]))
            source_ids.append(float(row[1]))
            target_ids.append(float(row[2]))
            sent_bytes.append(float(row[3]))
            total_size_bytes.append(float(row[4]))
            start_time.append(float(row[5]))
            end_time.append(float(row[6]))
            duration.append(float(row[7]))
            completed.append(row[8] == 'TRUE')

            if len(row) != 9:
                print("Invalid row: ", row)
                exit()

        print("Calculating statistics...")

        statistics = {
            'general_num_flows': len(flow_ids),
            'general_num_unique_sources': len(set(source_ids)),
            'general_num_unique_targets': len(set(target_ids)),
            'general_flow_size_bytes_mean': np.mean(total_size_bytes),
            'general_flow_size_bytes_std': np.std(total_size_bytes)
        }

        range_low =                     [-1,            -1,            -1,              100000,     2434900,            1000000,    10000000]
        range_high =                    [-1,            100000,        2434900,         -1,         -1,                 -1,         -1]
        range_name =                    ["all",         "less_100KB",  "less_2.4349MB", "geq_100KB", "geq_2.4349MB",    "geq_1MB",  "geq_10MB"]
        range_completed_duration =      [[],            [],            [],              [],         [],                 [],         []]
        range_completed_throughput =    [[],            [],            [],              [],         [],                 [],         []]
        range_num_finished_flows =      [0,             0,             0,               0,          0,                  0,          0]
        range_num_unfinished_flows =    [0,             0,             0,               0,          0,                  0,          0]
        range_low_eq =                  [0,             0,             0,               1,          1,                  1,          1,]
        range_high_eq =                 [0,             0,             0,               1,          1,                  1,          1,]
        # Go over all flows
        for i in range(0, len(flow_ids)):

            # Range-specific
            for j in range(0, len(range_name)):
                if (
                        (range_low[j] == -1 or (range_low_eq[j] == 0 and total_size_bytes[i] > range_low[j]) or (range_low_eq[j] == 1 and total_size_bytes[i] >= range_low[j])) and
                        (range_high[j] == -1 or (range_high_eq[j] == 0 and total_size_bytes[i] < range_high[j]) or (range_high_eq[j] == 1 and total_size_bytes[i] <= range_high[j]))
                ):
                    if completed[i]:
                        range_num_finished_flows[j] += 1
                        range_completed_duration[j].append(duration[i])
                        range_completed_throughput[j].append(total_size_bytes[i] * 8 / duration[i])

                else:
                    range_num_unfinished_flows[j] += 1

        # Ranges statistics
        for j in range(0, len(range_name)):

            # Number of finished flows
            statistics[range_name[j] + '_num_flows'] = range_num_finished_flows[j] + range_num_unfinished_flows[j]
            statistics[range_name[j] + '_num_finished_flows'] = range_num_finished_flows[j]
            statistics[range_name[j] + '_num_unfinished_flows'] = range_num_unfinished_flows[j]
            total = (range_num_finished_flows[j] + range_num_unfinished_flows[j])
            if range_num_finished_flows[j] != 0:
                statistics[range_name[j] + '_flows_completed_fraction'] = float(range_num_finished_flows[j]) / float(total)
                statistics[range_name[j] + '_mean_fct_ns'] = np.mean(range_completed_duration[j])
                statistics[range_name[j] + '_median_fct_ns'] = np.median(range_completed_duration[j])
                statistics[range_name[j] + '_99th_fct_ns'] = np.percentile(range_completed_duration[j], 99)
                statistics[range_name[j] + '_99.9th_fct_ns'] = np.percentile(range_completed_duration[j], 99.9)
                statistics[range_name[j] + '_mean_fct_ms'] = statistics[range_name[j] + '_mean_fct_ns'] / 1000000
                statistics[range_name[j] + '_median_fct_ms'] = statistics[range_name[j] + '_median_fct_ns'] / 1000000
                statistics[range_name[j] + '_99th_fct_ms'] = statistics[range_name[j] + '_99th_fct_ns'] / 1000000
                statistics[range_name[j] + '_99.9th_fct_ms'] = statistics[range_name[j] + '_99.9th_fct_ns'] / 1000000
                statistics[range_name[j] + '_throughput_mean_Gbps'] = np.mean(range_completed_throughput[j])
                statistics[range_name[j] + '_throughput_median_Gbps'] = np.median(range_completed_throughput[j])
                statistics[range_name[j] + '_throughput_99th_Gbps'] = np.percentile(range_completed_throughput[j], 99)
                statistics[range_name[j] + '_throughput_99.9th_Gbps'] = np.percentile(range_completed_throughput[j], 99.9)
                statistics[range_name[j] + '_throughput_1th_Gbps'] = np.percentile(range_completed_throughput[j], 1)
                statistics[range_name[j] + '_throughput_0.1th_Gbps'] = np.percentile(range_completed_throughput[j], 0.1)
            else:
                statistics[range_name[j] + '_flows_completed_fraction'] = 0

        # Print raw results
        print('Writing to result file flow_completion.statistics...')
        with open(analysis_folder_path + '/flow_completion.statistics', 'w+') as outfile:
            for key, value in sorted(statistics.items()):
                outfile.write(str(key) + "=" + str(value) + "\n")


##################################
# Analyze port utilization
#
def analyze_port_utilization():
    with open(run_folder_path + '/port_utilization.csv.log') as file:
        reader = csv.reader(file)

        # Column lists
        source_ids = []
        target_ids = []
        attached_to_server = []
        utilized_ns = []
        utilization = []
        utilization_server_ports = []
        utilization_non_server_ports = []
        num_server_port_zero = 0
        num_non_server_port_zero = 0

        print("Reading in port utilization log file...")

        # Read in column lists
        for row in reader:
            source_ids.append(float(row[0]))
            target_ids.append(float(row[1]))
            attached_to_server.append(row[2] == 'Y')
            utilized_ns.append(float(row[3]))
            utilization.append(float(row[4]))
            if row[2] == 'Y':
                utilization_server_ports.append(float(row[4]))
                if float(row[4]) == 0:
                    num_server_port_zero += 1
            else:
                utilization_non_server_ports.append(float(row[4]))
                if float(row[4]) == 0:
                    num_non_server_port_zero += 1

            if len(row) != 5:
                print("Invalid row: ", row)
                exit()

        print("Calculating statistics...")

        # General statistics (there is always a server port)
        statistics = {

            'all_port_num': len(source_ids),
            'all_port_unique_sources': len(set(source_ids)),
            'all_port_unique_targets': len(set(target_ids)),
            'all_port_mean_utilization': np.mean(utilization),
            'all_port_median_utilization': np.median(utilization),
            'all_port_std_utilization': np.std(utilization),
            'all_port_99th_utilization': np.percentile(utilization, 99),
            'all_port_99.9th_utilization': np.percentile(utilization, 99.9),

            'server_port_num': len(utilization_server_ports),
            'server_port_zero_num': num_server_port_zero,
            'server_port_mean_utilization': np.mean(utilization_server_ports),
            'server_port_median_utilization': np.median(utilization_server_ports),
            'server_port_std_utilization': np.std(utilization_server_ports),
            'server_port_99th_utilization': np.percentile(utilization_server_ports, 99),
            'server_port_99.9th_utilization': np.percentile(utilization_server_ports, 99.9)

        }

        # Only print non-server port statistics if they exist
        statistics['non_server_port_num'] = len(utilization_non_server_ports)
        if len(utilization_non_server_ports) > 0:
            statistics['non_server_ports_zero_num'] = num_non_server_port_zero
            statistics['non_server_port_mean_utilization'] = np.mean(utilization_non_server_ports)
            statistics['non_server_port_median_utilization'] = np.median(utilization_non_server_ports)
            statistics['non_server_port_std_utilization'] = np.std(utilization_non_server_ports)
            statistics['non_server_port_99th_utilization'] = np.percentile(utilization_non_server_ports, 99)
            statistics['non_server_port_99.9th_utilization'] = np.percentile(utilization_non_server_ports, 99.9)

        # Print raw results
        print('Writing to result file port_utilization.statistics...')
        with open(analysis_folder_path + '/port_utilization.statistics', 'w+') as outfile:
            for key, value in sorted(statistics.items()):
                outfile.write(str(key) + "=" + str(value) + "\n")

#analyze IAT and Burst:
def Flow_initiate(flows):
    with open(run_folder_path + '/flow_completion.csv.log') as FCT_file:
        with open (run_folder_path + "/flow_IAT.csv.log") as IAT_file:
            FCT_Reader = csv.reader(FCT_file)
            for row in FCT_Reader:
                flow_id = int(row[0])
                source_id = int(row[1])
                target_id = int(row[2])
                sent_bytes = float(row[3])
                total_size_bytes = float(row[4])
                start_time = float(row[5])
                end_time = float(row[6])
                duration = float(row[7])
                completed = row[8] == 'TRUE'
                flows[flow_id] = Flow(flow_id,source_id,target_id,sent_bytes,total_size_bytes,start_time,end_time,duration,completed)
            IAT_Reader = csv.reader(IAT_file)
            for row in IAT_Reader:
                flow_id = int(row[1])
                seq_num = int(row[2])
                byte = float(row[3])
                timeNs = float(row[4])
                flow = flows[flow_id]
                #first pkt
                if(flow.current_pkt_time == 0.0):
                    flow.current_pkt_time = timeNs
                    flow.IATs.append(0.0)
                    flow.current_seqnum = seq_num
                elif(flow.current_seqnum == seq_num):
                    continue
                else:
                    flow.IATs.append(timeNs-flow.current_pkt_time)
                    flow.current_pkt_time = timeNs
                    flow.current_seqnum = seq_num
                flow.pkt_bytes.append(byte)
                flow.ArrivalTimes.append(timeNs)
                flow.sent_bytes += byte
    with open(run_folder_path + "/flowset_num_flowID.csv.log") as weight_file:
        weight_reader = csv.reader(weight_file)
        for row in weight_reader:
            id = int(row[0])
            weight = float(row[2])
            flows[id].weight=weight
def analyze_IAT(flows):
    L = 2
    for flow in flows.values():
        if len(flow.IATs) > 1:
            flow.calcu_rate()
            flow.IATs[0] = flow.IATs[1]/2.0
            flow.IATs[1] = flow.IATs[0]
            flow.calcu_pp(L)
    sorted_flows = sorted(flows.values(),key=lambda x:x.ave_rate,reverse=True)
    print('Writing to result file pktIAT.statistics...')
    with open(analysis_folder_path+"/pkt_IAT.statistics"+str(L)+".csv","w",newline = '') as csvfile:
        writer = csv.writer(csvfile)
        for flow in flows.values():
            writer.writerow([flow.flowID,flow.p00,flow.p01,flow.p10,flow.p11,flow.pp,flow.burst_duration/1000.0,flow.burst_bytes,flow.duration/1000.0,flow.sent_bytes,len(flow.IATs)])

#input: unit(Ns), output: a serial of Throughput
def analyzeThroughput_Unit(flows,UnitNs,NumUnit):
    throughputs = {}
    for flowid in flows.keys():
        throughputs[flowid] = []
    for id in flows.keys():
        flow = flows[id]
        current_down_thresh = 0
        current_upthresh = UnitNs+current_down_thresh
        current_bytes = 0
        for i in range(0,len(flow.ArrivalTimes)):        #should remove syn pkt from serial
            time = flow.ArrivalTimes[i]
            bytes = flow.pkt_bytes[i]
            while(time >= current_upthresh):
                current_down_thresh = current_upthresh
                current_upthresh = current_down_thresh+UnitNs
                throughputs[id].append(current_bytes)
                current_bytes = 0
            current_bytes+= bytes
        if len(throughputs[id])<NumUnit:
            throughputs[id].append(current_bytes)
        while len(throughputs[id])<NumUnit:
            throughputs[id].append(0)
    return throughputs

def analyze_throughput_and_NFM(flows):
    Units = [1000*1000*1000,100*1000*1000,1000*1000,500*1000,200*1000,100*1000]
    total_time = 1000*1000*1000
    meanNFMs={}
    medianNFMs={}
    NFMs99 = {}
    NFMs9999 = {}
    NFMs001 = {}
    NFMs00001 = {}

    meanThrouputs={}
    medianThrouputs={}
    Throuputs99 = {}
    Throuputs9999 = {}
    Throuputs001 = {}
    Throuputs00001 = {}
    NFM = []
    for unit in Units:
        NFM.clear()
        NumUnit = math.ceil(total_time * 1.0 / unit)
        totalSpeedUnit = 1.25 * unit
        throuputs = analyzeThroughput_Unit(flows,unit,NumUnit)
        total_throuputs = [0]*NumUnit
        with open(analysis_folder_path + '/throughputs_'+str(unit)+".statics","w") as f:
            for k,v in throuputs.items():
                f.write("FlowId: "+str(k)+"\n")
                for i in range(0,len(v)):
                    t = v[i]
                    total_throuputs[i] += t
                    f.write(str(t)+"\t")
                f.write("\n")
        if NumUnit == 1:
            with open(analysis_folder_path + '/throughputs_perflow.statics.csv','w',newline='') as f:
                writer = csv.writer(f)
                writer.writerow(['flowid',"throuputs"])
                for k,v in throuputs.items():
                    writer.writerow([k,v[0]])
                ths = []
                for v in throuputs.values():
                    ths.append(v)
                thmean = np.mean(ths)
                writer.writerow(['mean',thmean])
                writer.writerow(['median',np.median(ths)])
        meanThrouputs[unit] = np.mean(total_throuputs)
        medianThrouputs[unit] = np.median(total_throuputs)
        Throuputs99[unit] = np.percentile(total_throuputs,99)
        Throuputs9999[unit] = np.percentile(total_throuputs,99.99)
        Throuputs001[unit] = np.percentile(total_throuputs,1)
        Throuputs00001[unit] = np.percentile(total_throuputs,0.01)
        with open(analysis_folder_path+"/NFM_"+str(unit)+".statics","w") as f:
            Max_diffs = [-1]*NumUnit
            ids = throuputs.keys()
            for i in range(0,len(ids)):
                for j in range(i+1,len(ids)):
                    th1 = throuputs[i]
                    th2 = throuputs[j]
                    weight1 = flows[i].weight
                    weight2 = flows[j].weight
                    for k in range(0,NumUnit):
                        if(th1[k] == 0 and th2[k] == 0):
                            continue
                        diffk = math.fabs(th1[k]*1.0/(totalSpeedUnit*weight1)-th2[k]*1.0/(totalSpeedUnit*weight2))
                        if diffk > Max_diffs[k]:
                            Max_diffs[k] = diffk
            for m in Max_diffs:
#                 NFM.append(m*1.0/(totalSpeedUnit*unit))
                NFM.append(m)
            for nfm in NFM:
                f.write(str(nfm)+"\n")
        meanNFMs[unit] = np.mean(NFM)
        medianNFMs[unit] = np.median(NFM)
        NFMs99[unit] = np.percentile(NFM,99)
        NFMs9999[unit] = np.percentile(NFM, 99.99)
        NFMs001[unit] = np.percentile(NFM,1)
        NFMs00001[unit] = np.percentile(NFM,0.01)
    with open(analysis_folder_path + "/NFM_Summary" + ".statics", "w") as f:
        for unit in Units:
            f.write(str(unit/1000)+"us \n")
            f.write("mean NFM: "+str(meanNFMs[unit])+"\n")
            f.write("median NFM: " + str(medianNFMs[unit])+"\n")
            f.write("99th NFM: " + str(NFMs99[unit])+"\n")
            f.write("99.99th NFM: " + str(NFMs9999[unit])+"\n")
            f.write("1th NFM: " + str(NFMs001[unit])+"\n")
            f.write("0.01th NFM: " + str(NFMs00001[unit])+"\n")
    with open(analysis_folder_path+"/Throuputs_Summary"+".statics","w") as f:
        for unit in Units:
            f.write(str(unit/1000)+"us \n")
            f.write("mean Throuputs: "+str(meanThrouputs[unit])+"\n")
            f.write("median Throuputs: " + str(medianThrouputs[unit])+"\n")
            f.write("99th Throuputs: " + str(Throuputs99[unit])+"\n")
            f.write("99.99th Throuputs: " + str(Throuputs9999[unit])+"\n")
            f.write("1th Throuputs: " + str(Throuputs001[unit])+"\n")
            f.write("0.01th Throuputs: " + str(Throuputs00001[unit])+"\n")

def analyze_ack_bytes():
    acked_bytes_dict = {}
    with open(run_folder_path + '/flow_completion.log') as file:
        lines = file.readlines()
        for line in lines:
            datas = line.split()
            flowid = datas[0]
            if(flowid == 'FlowId'):
                continue
            acked_byte = int(datas[3])
            acked_bytes_dict[flowid] = acked_byte
    with open(analysis_folder_path+"/flow_acked_bytes"+".statics.csv","w") as f:
        writer = csv.writer(f)
        writer.writerow(['flowid','acked_bytes'])
        for k,v in acked_bytes_dict.items():
            writer.writerow([k,v])
        ackedbytes = []
        for v in acked_bytes_dict.values():
            ackedbytes.append(v)
        mean = np.mean(ackedbytes)
        writer.writerow(['mean',mean])
        writer.writerow(['median',np.median(ackedbytes)])


def analyze_Inflight_Perflow(flows):
    inflight_bytes={}
    for id in flows.keys():
        inflight_bytes[id] = []
    with open(run_folder_path + '/Inflight_Bytes.csv.log') as file:
        reader = csv.reader(file)
        for row in reader:
            flowid = int(row[0])
            inflight = int(row[1])
            inflight_bytes[flowid].append(inflight)
    mean_inflight_bytes = {}
    median_inflight_bytes = {}
    for k,v in inflight_bytes.items():
        mean_inflight_bytes[k] = np.mean(v)
        median_inflight_bytes[k] = np.median(v)
    with open(analysis_folder_path+'/CWND.statics.csv','w',newline='') as CWNDFile:
        writer = csv.writer(CWNDFile)
        writer.writerow(['flowid','mean_cwnd','median_cwnd'])
        for id in flows.keys():
            mean = mean_inflight_bytes[id]
            median = median_inflight_bytes[id]
            writer.writerow([id,mean,median])

def analyze_total_drop_rate(flows, interval):
    # interval = 10000000
    interval_num = int(1000000000/interval)
    time = interval
    enqueue = [0]
    full_drop = [0]
    schedule_drop = [0]
    with open(run_folder_path+"/enqueue_event.csv.log") as ENQUE:
        with open(run_folder_path+"/drop_event.csv.log") as DROP:
            Enqueue_Reader = csv.reader(ENQUE)
            Drop_Reader = csv.reader(DROP)
            i = 0
            for row in Enqueue_Reader:
                if(int(row[5]) <= time):
                    enqueue[i] += 1
                else:
                    while(int(row[5]) > time):
                        time += interval
                        i += 1
                        enqueue.append(0)
                    enqueue[i] += 1
            time = interval
            i = 0
            for row in Drop_Reader:
                if(int(row[5]) <= time):
                    if(int(row[7]) == 0):
                        full_drop[i] += 1
                    else:
                        schedule_drop[i] += 1
                else:
                    while(int(row[5]) > time):
                        time += interval
                        i += 1
                        full_drop.append(0)
                        schedule_drop.append(0)
                    if(int(row[7]) == 0):
                        full_drop[i] += 1
                    else:
                        schedule_drop[i] += 1
            enqueue_len = interval_num-len(enqueue)
            schedule_len = interval_num-len(schedule_drop)
            for i in range(enqueue_len):
                enqueue.append(0)
            for i in range(schedule_len):
                full_drop.append(0)
                schedule_drop.append(0)
#             with open(analysis_folder_path + "/enqueue_per_slice","w") as f:
#                 f.write("enqueue: "+",".join('%s' %id for id in enqueue)+"\n")
#                 f.write("schedule_drop: "+",".join('%s' %id for id in schedule_drop)+"\n")
#                 f.write("full_drop: "+",".join('%s' %id for id in full_drop)+"\n")
            with open(analysis_folder_path + "/drop_rate_total.statistics","w") as f:
                full_rate = []
                schedule_rate = []
                for i in range(interval_num):
                    receive = full_drop[i]+schedule_drop[i]+enqueue[i]
                    if(receive != 0):
                        full_rate.append(full_drop[i]/receive)
                        schedule_rate.append(schedule_drop[i]/receive)
                    else:
                        full_rate.append(-1)
                        schedule_rate.append(-1)
                f.write("full_drop_total: "+" ".join('%s' %id for id in full_rate)+"\n")
                receive = sum(enqueue) + sum(full_drop) + sum(schedule_drop)
                if(receive!=0):
                    f.write("average_full_drop: "+str(sum(full_drop)/receive)+"\n")
                else:
                    f.write("average_full_drop: "+"None"+"\n")
                f.write("schedule_drop_total: "+" ".join('%s' %id for id in schedule_rate)+"\n")
                if(receive!=0):
                    f.write("average_schedule_drop: "+str(sum(schedule_drop)/receive)+"\n")
                else:
                    f.write("average_schedule_drop: "+"None"+"\n")
                f.write("average_final_drop: "+str((sum(schedule_drop)+sum(full_drop))/receive)+"\n")


def analyze_perflow_drop_rate(flows, interval):
    # interval = 10000000
    interval_num = int(1000000000/interval)
    time = interval
    enqueue_perf = {}
    full_perf = {}
    schedule_perf = {}
    for flowid in flows.keys():
        enqueue_perf[flowid] = [0]
        full_perf[flowid] = [0]
        schedule_perf[flowid] = [0]
    with open(run_folder_path+"/enqueue_event.csv.log") as ENQUE:
        with open(run_folder_path+"/drop_event.csv.log") as DROP:
            Enqueue_Reader = csv.reader(ENQUE)
            Drop_Reader = csv.reader(DROP)
            i = 0
            for row in Enqueue_Reader:
                if(int(row[5]) <= time):
                    enqueue_perf[int(row[2])][i] += 1
                else:
                    while(int(row[5]) > time):
                        time += interval
                        i += 1
                        for flowid in flows.keys():
                            enqueue_perf[flowid].append(0)
                    enqueue_perf[int(row[2])][i] += 1
            time = interval
            i = 0
            for row in Drop_Reader:
                if(int(row[5]) <= time):
                    if(int(row[7]) == 0):
                        full_perf[int(row[2])][i] += 1
                    else:
                        schedule_perf[int(row[2])][i] += 1
                else:
                    while(int(row[5]) > time):
                        time += interval
                        i += 1
                        for flowid in flows.keys():
                            full_perf[flowid].append(0)
                            schedule_perf[flowid].append(0)
                    if(int(row[7]) == 0):
                        full_perf[int(row[2])][i] += 1
                    else:
                        schedule_perf[int(row[2])][i] += 1
            enqueue_len = interval_num-len(enqueue_perf[0])
            schedule_len = interval_num-len(schedule_perf[0])
            for i in range(enqueue_len):
                for flowid in flows.keys():
                    enqueue_perf[flowid].append(0)
            for i in range(schedule_len):
                for flowid in flows.keys():
                    full_perf[flowid].append(0)
                    schedule_perf[flowid].append(0)
            with open(analysis_folder_path + "/drop_rate_per_flow.statistics","w") as f:
                for flowid in flows.keys():
                    # f.write(str(enqueue_perf[flowid][0])+",")
                    f.write("flow_id: "+str(flowid)+"\n")
                    full_rate = []
                    schedule_rate = []
                    for i in range(interval_num):
                        receive = full_perf[flowid][i]+schedule_perf[flowid][i]+enqueue_perf[flowid][i]
                        if(receive != 0):
                            full_rate.append(full_perf[flowid][i]/receive)
                            schedule_rate.append(schedule_perf[flowid][i]/receive)
                        else:
                            full_rate.append(-1)
                            schedule_rate.append(-1)
                    f.write("full_drop: "+" ".join('%s' %id for id in full_rate)+"\n")
                    receive = sum(enqueue_perf[flowid]) + sum(full_perf[flowid]) + sum(schedule_perf[flowid])
                    if(receive!=0):
                        f.write("average_full_drop: "+str(sum(full_perf[flowid])/receive)+"\n")
                    else:
                        f.write("average_full_drop: "+"None"+"\n")
                    f.write("schedule_drop: "+" ".join('%s' %id for id in schedule_rate)+"\n")
                    if(receive!=0):
                        f.write("average_schedule_drop: "+str(sum(schedule_perf[flowid])/receive)+"\n")
                    else:
                        f.write("average_schedule_drop: "+"None"+"\n")
                    f.write("\n")

def analyze_pifo_total_drop_rate(flows, interval):
    # interval = 10000000
    interval_num = int(1000000000/interval)
    time = interval
    enqueue = [0]
    full_drop = [0]
    schedule_drop = [0]
    with open(run_folder_path+"/enqueue_event.csv.log") as ENQUE:
        with open(run_folder_path+"/drop_event.csv.log") as DROP:
            Enqueue_Reader = csv.reader(ENQUE)
            Drop_Reader = csv.reader(DROP)
            i = 0
            for row in Enqueue_Reader:
                if(int(row[5]) <= time):
                    enqueue[i] += 1
                else:
                    while(int(row[5]) > time):
                        time += interval
                        i += 1
                        enqueue.append(0)
                    enqueue[i] += 1
            time = interval
            i = 0
            for row in Drop_Reader:
                if(int(row[5]) <= time):
                    if(int(row[7]) == 0):
                        full_drop[i] += 1
                    else:
                        schedule_drop[i] += 1
                else:
                    while(int(row[5]) > time):
                        time += interval
                        i += 1
                        full_drop.append(0)
                        schedule_drop.append(0)
                    if(int(row[7]) == 0):
                        full_drop[i] += 1
                    else:
                        schedule_drop[i] += 1
            enqueue_len = interval_num-len(enqueue)
            schedule_len = interval_num-len(schedule_drop)
            for i in range(enqueue_len):
                enqueue.append(0)
            for i in range(schedule_len):
                full_drop.append(0)
                schedule_drop.append(0)
#             with open(analysis_folder_path + "/enqueue_per_slice","w") as f:
#                 f.write("enqueue: "+",".join('%s' %id for id in enqueue)+"\n")
#                 f.write("schedule_drop: "+",".join('%s' %id for id in schedule_drop)+"\n")
#                 f.write("full_drop: "+",".join('%s' %id for id in full_drop)+"\n")
            with open(analysis_folder_path + "/drop_rate_total_pifo.statistics","w") as f:
                full_rate = []
                schedule_rate = []
                for i in range(interval_num):
                    receive = enqueue[i]
                    if(receive != 0):
                        full_rate.append(full_drop[i]/receive)
                        schedule_rate.append(schedule_drop[i]/receive)
                    else:
                        full_rate.append(-1)
                        schedule_rate.append(-1)
                f.write("full_drop_total: "+" ".join('%s' %id for id in full_rate)+"\n")
                receive = sum(enqueue)
                if(receive!=0):
                    f.write("average_full_drop: "+str(sum(full_drop)/receive)+"\n")
                else:
                    f.write("average_full_drop: "+"None"+"\n")
                f.write("schedule_drop_total: "+" ".join('%s' %id for id in schedule_rate)+"\n")
                if(receive!=0):
                    f.write("average_schedule_drop: "+str(sum(schedule_drop)/receive)+"\n")
                else:
                    f.write("average_schedule_drop: "+"None"+"\n")
                f.write("average_final_drop: "+str((sum(schedule_drop)+sum(full_drop))/receive)+"\n")


def analyze_pifo_perflow_drop_rate(flows, interval):
    # interval = 10000000
    interval_num = int(1000000000/interval)
    time = interval
    enqueue_perf = {}
    full_perf = {}
    schedule_perf = {}
    for flowid in flows.keys():
        enqueue_perf[flowid] = [0]
        full_perf[flowid] = [0]
        schedule_perf[flowid] = [0]
    with open(run_folder_path+"/enqueue_event.csv.log") as ENQUE:
        with open(run_folder_path+"/drop_event.csv.log") as DROP:
            Enqueue_Reader = csv.reader(ENQUE)
            Drop_Reader = csv.reader(DROP)
            i = 0
            for row in Enqueue_Reader:
                if(int(row[5]) <= time):
                    enqueue_perf[int(row[2])][i] += 1
                else:
                    while(int(row[5]) > time):
                        time += interval
                        i += 1
                        for flowid in flows.keys():
                            enqueue_perf[flowid].append(0)
                    enqueue_perf[int(row[2])][i] += 1
            time = interval
            i = 0
            for row in Drop_Reader:
                if(int(row[5]) <= time):
                    if(int(row[7]) == 0):
                        full_perf[int(row[2])][i] += 1
                    else:
                        schedule_perf[int(row[2])][i] += 1
                else:
                    while(int(row[5]) > time):
                        time += interval
                        i += 1
                        for flowid in flows.keys():
                            full_perf[flowid].append(0)
                            schedule_perf[flowid].append(0)
                    if(int(row[7]) == 0):
                        full_perf[int(row[2])][i] += 1
                    else:
                        schedule_perf[int(row[2])][i] += 1
            enqueue_len = interval_num-len(enqueue_perf[0])
            schedule_len = interval_num-len(schedule_perf[0])
            for i in range(enqueue_len):
                for flowid in flows.keys():
                    enqueue_perf[flowid].append(0)
            for i in range(schedule_len):
                for flowid in flows.keys():
                    full_perf[flowid].append(0)
                    schedule_perf[flowid].append(0)
            with open(analysis_folder_path + "/drop_rate_per_flow_pifo.statistics","w") as f:
                for flowid in flows.keys():
                    # f.write(str(enqueue_perf[flowid][0])+",")
                    f.write("flow_id: "+str(flowid)+"\n")
                    full_rate = []
                    schedule_rate = []
                    for i in range(interval_num):
                        receive = enqueue_perf[flowid][i]
                        if(receive != 0):
                            full_rate.append(full_perf[flowid][i]/receive)
                            schedule_rate.append(schedule_perf[flowid][i]/receive)
                        else:
                            full_rate.append(-1)
                            schedule_rate.append(-1)
                    f.write("full_drop: "+" ".join('%s' %id for id in full_rate)+"\n")
                    receive = sum(enqueue_perf[flowid])
                    if(receive!=0):
                        f.write("average_full_drop: "+str(sum(full_perf[flowid])/receive)+"\n")
                    else:
                        f.write("average_full_drop: "+"None"+"\n")
                    f.write("schedule_drop: "+" ".join('%s' %id for id in schedule_rate)+"\n")
                    if(receive!=0):
                        f.write("average_schedule_drop: "+str(sum(schedule_perf[flowid])/receive)+"\n")
                    else:
                        f.write("average_schedule_drop: "+"None"+"\n")
                    f.write("\n")





def analyze_buffer_util(flows):
    buffer_util = []
    with open(run_folder_path+"/dequeue_event.csv.log") as DEQUE:
        Dequeue_Reader = csv.reader(DEQUE)
        for row in Dequeue_Reader:
            buffer_util.append(float(row[7]))
    with open(analysis_folder_path + "/buffer_utilization.statistics","w") as f:
        f.write("average_buffer_utilization: " + str(np.mean(buffer_util)) + "\n")
        f.write("median_buffer_util: " + str(np.median(buffer_util)) + "\n")





            # Call analysis functions
flows = {}
analyze_flow_completion()
analyze_port_utilization()
Flow_initiate(flows)
# analyze_IAT(flows)
analyze_throughput_and_NFM(flows)
analyze_ack_bytes()
analyze_Inflight_Perflow(flows)
analyze_pifo_total_drop_rate(flows, 10000000)
# analyze_pifo_perflow_drop_rate(flows, 10000000)
analyze_buffer_util(flows)

os.system("rm -f " +run_folder_path + "/dequeue_event.csv.log")
os.system("rm -f " +run_folder_path + "/enqueue_event.csv.log")
os.system("rm -f " +run_folder_path + "/drop_event.csv.log")
os.system("rm -f " +run_folder_path + "/flow_IAT.csv.log")
os.system("rm -f " +run_folder_path + "/Inflight_Bytes.csv.log")
