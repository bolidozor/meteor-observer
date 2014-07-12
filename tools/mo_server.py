from __future__ import print_function
import tornado.ioloop
import tornado.web
import re

vector_re = re.compile("^\[([\d\.\-E]+)\,\ ([\d\.\-E]+)\,\ ([\d\.\-E]+)\]$")

def parse_vector(str):
    result = vector_re.match(str)
    return float(result.group(1)), float(result.group(2)), float(result.group(3))

class ReportHandler(tornado.web.RequestHandler):
    def get(self):
        time = int(self.get_argument('time'))
        beg_x, beg_y, beg_z = parse_vector(self.get_argument('trail_beg'))
        end_x, end_y, end_z = parse_vector(self.get_argument('trail_end'))
        loc_lat, loc_long = float(self.get_argument('loc_lat')), \
                            float(self.get_argument('loc_long'))
        observer_id = self.get_argument('observer_id')

        print ("%d, %f, %f, %f, %f, %f, %f, %f, %f, %s" % (time, beg_x, beg_y, beg_z, end_x, end_y, \
                                                          end_z, loc_lat, loc_long, observer_id))

        self.write("ACK")

application = tornado.web.Application([
    (r"/report", ReportHandler),
])

if __name__ == "__main__":
    application.listen(3003)
    tornado.ioloop.IOLoop.instance().start()
